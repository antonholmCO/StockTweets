// Assign value to setMarket = market sector that populates treemap
let setMarket = 'medical'

// Object to store all symbols recived from http://stocktweets.rocks/api/v1/symbols/${market[sector]} when page is loaded
let big_data = {
    'tech': {},
    'medical': {}
}

// Array used to store all symbols, marketcap, market percentage share to order and filter for treemap
const sort_list = []

// Button group to toggle between markets
const buttonElem = `<div class="btn-group mb-2 m-auto" role="group" aria-label="Basic example">
                <button type="button" onclick="updateTreemap('tech')" class="btn btn-dark border-light">Tech</button>
                <button type="button" onclick="updateTreemap('medical')" class="btn btn-dark border-light">Medical</button>
            </div>`

// Websocket connection to finnhub
const socket = new WebSocket('wss://ws.finnhub.io?token=c70ab7qad3id7ammkm3g');


// Document ready, request symbols, create element buttongroup on index and timeout function to delay build of treemap until symbols are recieved 
$(document).ready(function () {
    requestSymbols()
    $('#buttonDIV').append(buttonElem);
    const myTimeout = setTimeout(updateTreemap, 2000);


});


// Runs on page load
function requestSymbols() {
    // Markets to request
    const market = ['tech', 'medical']
    let total_market_cap = 0

    // Get symbols
    for (let sector in market) {
        // Set total marketcap to zero each iteration
        let total_market_cap = 0
        
        $.ajax({
            url: `http://stocktweets.rocks/api/v1/symbols/${market[sector]}`,
            headers: { "Accept": "application/json" }
        })
            .done(function (data) {

                // Add marketcap to total marketcap for each stock in market sector
                // Create big_data object of all symbols and marketcap 
                
                for (let stock in data) {
                    total_market_cap = total_market_cap + data[stock]['marketcap'];
                    big_data[market[sector]][data[stock]['symbol']] = {
                        'marketcap': data[stock]['marketcap']
                    }
                }
                
                // For each stock requested, calculate marketshare in % and assign boolean to see if it should be in treemap
                for (let symbol in data) {
                    let market_share = data[symbol]['marketcap'] / total_market_cap
                    sort_list.push({ 'marketSector': market[sector], 'name': data[symbol]['symbol'], 'marketcap': data[symbol]['marketcap'], 'marketPercentage': market_share, 'inList': false })
                }

                
            })
        delete total_market_cap
    }   

};

// function to build treemap, runs once at pageload and when market sector buttons are clicked in button group 
function updateTreemap(marketSector) {
    
    
    if (!marketSector) {
        marketSector = 'tech'
    }
    
    // Sort and order sort_list array by market cap for treemap, biggest marketcap at top
    if (marketSector != setMarket) {
        setMarket = marketSector

        if ($(`.treeMapRows`)) {
            $(`#treemap .treeMapRows`).remove()
        }

        function compare(a, b) {
            if (a.marketcap > b.marketcap) {
                return -1;
            }
            if (a.marketcap < b.marketcap) {
                return 1;
            }
            return 0;
        }

        sort_list.sort(compare);


        // creates elements for each coin
        
        let elementCreator = ''
        let width = 0
        let height = 0
        
        // Create boxes based on % size 
        let targetArray = [0.25, 0.25, 0.2, 0.15, 0.15]
        let layout = [0.25, 0.25, 0.2, 0.15, 0.15]
        // Stocks are sorted into listForTreemap, each object in list has a size to fill and stocks are filtered to fill each element based on market percentage
        let listForTreemap = [{ '0': [] }, { '1': [] }, { '2': [] }, { '3': [] }, { '4': [] }]
        // Creates a temporary list to sort stocks for treemap so sort_list can be used for other purposes if necessary
        temporaryList = Array.from(sort_list)

        // Toggle boolean
        for (let inlist in temporaryList) {
            temporaryList[inlist]['inList'] = false
        }
        let money = 0
        // For all sizes in target array
        for (let i in targetArray) {

            // Filter all stocks for size...
            for (let b in temporaryList) {
                // only if they are in right market sector
                if (temporaryList[b]['marketSector'] === setMarket) {

                    // If element is to big to fit, continue
                    if (targetArray[i] - temporaryList[b]['marketPercentage'] <= 0) {
                        continue;
                    } else if (temporaryList[b]['inList'] === false) {
                        // If it fits, push to list, subtract percentage from targetarray and toggle boolean
                        listForTreemap[i][i].push(temporaryList[b])
                        targetArray[i] = targetArray[i] - temporaryList[b]['marketPercentage']
                        temporaryList[b]['inList'] = true
                    }

                } 


            }
        }

        // For each size in target array
        for (let ind in targetArray) {

            let elemArea = layout[ind]
            let elemWidth = 1000
            let elemHeight = elemArea * 1000
            let row = ''
            let sharOfArea = 0
            let boxes = ''

            // For each object int listForTreemap
            for (let box in listForTreemap[ind]) {
                sharOfArea = 0

                // For each stock in objects array assign a shareOfArea
                for (let totalElemShare in listForTreemap[ind][box]) {
                    let percentage = listForTreemap[ind][box][totalElemShare]['marketPercentage']
                    sharOfArea = sharOfArea + percentage
                }

                
                // Create element, height is preset from targetArray percentage, widht is calculated as percentage of share from total marketcap
                for (let item in listForTreemap[ind][box]) {
                    
                    height = elemHeight
                    width = (listForTreemap[ind][box][item]['marketPercentage'] / sharOfArea) * 100
                    // HTML code for each element
                    let text = `<div id="${listForTreemap[ind][box][item]['name']}" class="treemap_element stock-element" style="width:${width}%; height:${height}px;" onmouseover="mouse_over(this.id)" onmouseout="mouse_out()" onclick="openModal(this.id)">
                    <h3 class="text-capitalize">${listForTreemap[ind][box][item]['name']}</h3><p><strong id="${listForTreemap[ind][box][item]['name']}price" class="text-light">Market Closed</strong></p><br></div>`
                    
                    // Add element to row
                    row = row + text

                }

                

            }
            // One row = one box, five boxes represent 100% of treemap 
            boxes = `<div class="treeMapRows row m-auto" style="width:${elemWidth}; height:${elemHeight}px;">${row}</div>`
            elementCreator = elementCreator + boxes

            
                
            
        }

        $(elementCreator).appendTo("#treemap");

    } else {
        // If allready on the requested view
        console.log('already on medical')
    }


};

// On hover element in treemap open extra info overlay
function mouse_over(element_id) {
    let s = big_data[setMarket][element_id]['marketcap']
    let marketCapOutput = s.toLocaleString()
    let new_element = `<div class="info-window"><h3 class="text-capitalize">${element_id}</h3> <p class="text-capitalize"><p>MarketCap: $${marketCapOutput}</p></div>`
    let info_window = $('#' + element_id)
    info_window.append(new_element);

}

// Mouse out from element close overlay
function mouse_out() {
    let info_window = $('.info-window')
    info_window.remove();

}


// On click treemap element. Request individual stock and twitter data
function openModal(stock) {
    let loaderElem = `<div class="loader"></div>`
    $(`#treemap`).append(loaderElem)
    $.ajax({
        url: `http://stocktweets.rocks/api/v1/stocktweet/${stock}`,
        headers: { "Accept": "application/json" }
    })
        .done(function (data) {
            let marketcap = ''

            for (let x in sort_list) {

                if (sort_list[x]['name'] === stock) {
                    marketcap = sort_list[x]['marketcap']
                }
            }

            let tweetArray = []

            // Create twitter element for each tweet
            for (let tweet in data['tweets']['data']) {

                let tweets = `<li class="bg-light p-1 rounded twitter-element">
                <img class="d-inline" style="width:32px;height:32px;"src="resources/twitterTransparent.png">
                <b><a class="text-decoration-none text-primary" href="https://twitter.com/${data['tweets']['data'][tweet]['authorName']}">${data['tweets']['data'][tweet]['authorName']}</a></b>
                <a class="text-decoration-none text-dark" href="https://twitter.com/${data['tweets']['data'][tweet]['authorName']}/status/${data['tweets']['data'][tweet]['id']}"><p>${data['tweets']['data'][tweet]['text']}</p></a></li>`
                tweetArray.push(tweets)
            }

            
            // Display stock info
            let marketCapOutput = marketcap.toLocaleString()
            let change = data['stock']['percentChange']
            
            if (change > 0) {
                text_color = 'text-success'
            } else {
                text_color = 'text-danger'
            }

            let new_element = `<div id="info-overlay-window" class="overlay bg-dark">
                <div class="row">
                <div class="col-lg-6 bg-dark stock-window-element border border-light border-5"><h1 class="text-light text-center text-capitalize">${data['stock']['companyName']}</h1>
                <div class="col-lg-8 m-auto">
                <ul class="list-unstyled text-light pt-4 mt-3 text-center">
                    <li><h3 class="border border-light p-3">Price in USD: $${data['stock']['priceUSD']}</h3></li>
                    <li><h3 class="border border-light p-3">Price in SEK: ${data['stock']['priceSEK']}sek</h3></li>
                    <li><h3 class="border border-light p-3">Marketcap: $${marketCapOutput}</h3></li>
                    
                    <li><h3 class="border border-light p-3 ${text_color}">Change: ${change}%</h3></li>
                    <li><h3 class="border border-light p-3">Symbol: ${data['stock']['symbol']}</h3></li>
                </ul>
               </div>
                
                </div>

                <div class="col-lg-6 twitter-window-element"><h1 class="text-light text-center">Tweets <img style="width:50px;height:50px;"src="resources/twitter.png"></h1>
                <h5 class="text-capitalize text-center text-light mt-2">Topic: ${stock}</h5>
                <div class="col-lg-10 m-auto">
                <ul class="list-unstyled">${tweetArray}</ul>
                </div>
                
                </div>
                
                <div class="col-lg-12 d-flex justify-content-center"><button class="btn btn-secondary btn-lg mt-5" onclick="close_overlay()">Close</button></div>
                </div>
                </div>`

            $('body').append(new_element);
            $('html, body').animate({ scrollTop: 0 }, 'fast');
        })

    
}

// Closes overlay with close button
function close_overlay() {
    let element = $('#info-overlay-window')
    element.remove();
    $(`.loader`).remove()
}





// Connection opened -> Subscribe to stream
socket.addEventListener('open', function (event) {
    let count = 0
    while (count < 49) {
        socket.send(JSON.stringify({ 'type': 'subscribe', 'symbol': sort_list[count]['name'] }))
        count++
    }


});




// Listen for messages
socket.addEventListener('message', function (event) {

    var msg = JSON.parse(event.data);
    console.log(msg)
    //Update treemap elements on message

    for (let symbol in msg['data']) {
        lastPrice = { 'symbol': msg['data'][symbol]['s'], 'lastprice': msg['data'][symbol]['p'] }

        // Assign color to treemap element based on stock price change
        if (parseInt(lastPrice['lastprice']) > parseInt($(`#${msg['data'][symbol]['s']}price`).text())) {
            $(`#${lastPrice['symbol']}`).css("background-color", "green")
        } else {
            $(`#${lastPrice['symbol']}`).css("background-color", "red")
        }
        $(`#${lastPrice['symbol']}price`).html(`$${lastPrice['lastprice']}`)
    }

});


// Can be used to unsubscribe webstream, NOT used in this implementation
function unsubscribe() {
    if (sort_list.length > 1) {
        // Unsubscribe
        for (let symbol in sort_list) {
            socket.send(JSON.stringify({ 'type': 'unsubscribe', 'symbol': sort_list[symbol]['name'] })) 
        }
    }
}



// Displays time for stockmarket NYSE and indicates if market is opened or closed
function startTime() {
    const today = new Date();
    const opens = new Date();
    const closes = new Date();
    opens.setHours(9);
    let opensTime = opens.getHours();
    closes.setHours(16);
    let closesTime = closes.getHours();

    let h = today.getHours() - 6;
    let m = today.getMinutes();
    let s = today.getSeconds();
    let day = today.getDay();
    m = checkTime(m);
    s = checkTime(s);
    document.getElementById('clock').innerHTML = h + ":" + m + ":" + s;
    
    if (h >= opensTime && h < closesTime) {
        
        if (day === 6 || day === 0) {
            $('#openClose').addClass("bg-danger").html('Closed')
            let hasClass = $('#openClose').hasClass("bg-success")
            if (hasClass) {
                $('#openClose').removeClass("bg-success")
            }
        } else {
            $('#openClose').addClass("bg-success").html('Open')
            let hasClass = $('#openClose').hasClass("bg-danger")
            if (hasClass) {
                $('#openClose').removeClass("bg-danger")
            }
        }
        
        
    } else {
        $('#openClose').addClass("bg-danger").html('Closed')
        let hasClass = $('#openClose').hasClass("bg-success")
        if (hasClass) {
            $('#openClose').removeClass("bg-success")
        }
        
    }

    setTimeout(startTime, 1000);
}

function checkTime(i) {
    if (i < 10) { i = "0" + i };  // add zero in front of numbers < 10
    return i;
}

