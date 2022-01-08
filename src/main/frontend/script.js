let scrollText = ''
let setMarket = 'medical'
let big_data = {
    'tech': {},
    'medical': {}
}
const sort_list = []
let newText = ''

const buttonElem = `<div class="btn-group mb-2 m-auto" role="group" aria-label="Basic example">
                <button type="button" onclick="updateTreemap('tech')" class="btn btn-primary">Tech</button>
                <button type="button" onclick="updateTreemap('medical')" class="btn btn-primary">Medical</button>
                <button type="button" class="btn btn-primary disabled">Disabled</button>
            </div>`

// Websocket
const socket = new WebSocket('wss://ws.finnhub.io?token=c70ab7qad3id7ammkm3g');

// Document ready
$(document).ready(function () {
    requestSymbols()
    $('#buttonDIV').append(buttonElem);
    const myTimeout = setTimeout(updateTreemap, 2000);


});

function requestSymbols() {

    const market = ['tech', 'medical']
    let total_market_cap = 0

    // Get symbols
    for (let sector in market) {
        total_market_cap = total_market_cap - total_market_cap
        $.ajax({
            url: `http://stocktweets.rocks/api/v1/symbols/${market[sector]}`,
            headers: { "Accept": "application/json" }
        })
            .done(function (data) {

                // Create object of all symbols and marketcap
                for (let stock in data) {
                    total_market_cap = total_market_cap + data[stock]['marketcap'];

                    big_data[market[sector]][data[stock]['symbol']] = {
                        'marketcap': data[stock]['marketcap']
                    }
                }

                
                for (let symbol in data) {
                    let market_share = data[symbol]['marketcap'] / total_market_cap
                    let market_percentage = (Math.round(market_share * 100)).toFixed(5);

                    sort_list.push({ 'marketSector': market[sector], 'name': data[symbol]['symbol'], 'marketcap': data[symbol]['marketcap'], 'marketPercentage': market_share, 'inList': false })
                }


            })

    }

    

};

function updateTreemap(marketSector) {
    
    if ($(`.treeMapRows`)) {
        $(`#treemap .treeMapRows`).remove()
    }
    
    if (!marketSector) {
        marketSector = 'tech'
    }
    
    // Sort and order by market cap for treemap
    if (marketSector != setMarket) {
        setMarket = marketSector

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
        let total_cap = 0
        let elementCreator = ''

        let treeMapWidth = $('#treemap').width();
        let treeMapHeight = $('#treemap').height();
        let totalArea = treeMapWidth * treeMapHeight
        let width = 150
        let height = 150
        let row = 0
        let maxWidth = 500
        let before = {
            'width': width,
            'height': 0,
            'row': 1000
        }
        let minHeight = 100
        boxes = ''
        
        // Create boxes
        let targetArray = [0.25, 0.25, 0.2, 0.15, 0.15]
        let layout = [0.25, 0.25, 0.2, 0.15, 0.15]

        let myList = [{ '0': [] }, { '1': [] }, { '2': [] }, { '3': [] }, { '4': [] }]
        
        temporaryList = Array.from(sort_list)
        for (let inlist in temporaryList) {
            temporaryList[inlist]['inList'] = false
        }

        for (let i in targetArray) {

            for (let b in temporaryList) {

                if (temporaryList[b]['marketSector'] === setMarket) {

                    if (targetArray[i] - temporaryList[b]['marketPercentage'] <= 0) {
                        continue;
                    } else if (temporaryList[b]['inList'] === false) {
                        myList[i][i].push(temporaryList[b])
                        targetArray[i] = targetArray[i] - temporaryList[b]['marketPercentage']
                        
                        temporaryList[b]['inList'] = true
                    }

                } else {
                    $(`#treemap #${temporaryList[b]['name']}`).remove()
                }


            }
        }

        for (let ind in targetArray) {

            let elemArea = layout[ind]
            let elemWidth = 1000
            let elemHeight = elemArea * 1000
            let row = ''
            let sharOfArea = 0
            let boxes = ''

            for (let box in myList[ind]) {
                sharOfArea = 0

                for (let totalElemShare in myList[ind][box]) {
                    let percentage = myList[ind][box][totalElemShare]['marketPercentage']
                    sharOfArea = sharOfArea + percentage
                }

                

                for (let item in myList[ind][box]) {
                    
                    height = elemHeight
                    width = (myList[ind][box][item]['marketPercentage'] / sharOfArea) * 100

                    // Sets class on element based on stockprice increased or decreased

                    //Creates element and appends to treemap element

                    let text = `<div id="${myList[ind][box][item]['name']}" class="treemap_element stock-element" style="width:${width}%; height:${height}px;" onmouseover="mouse_over(this.id)" onmouseout="mouse_out()" onclick="openModal(this.id)">
                    <h3 class="text-capitalize">${myList[ind][box][item]['name']}</h3><p id="${myList[ind][box][item]['name']}price"></p><br></div>`
                    console.log(myList[ind][box][item])
                    row = row + text

                }

                

            }

            boxes = `<div class="treeMapRows row m-auto" style="width:${elemWidth}; height:${elemHeight}px;">${row}</div>`
            elementCreator = elementCreator + boxes

            
                
            
        }
        $(elementCreator).appendTo("#treemap");
    }


};


function mouse_over(element_id) {

    let new_element = `<div class="info-window"><h3 class="text-capitalize">${element_id}</h3> <p class="text-capitalize">Name: ${element_id}</p> <p>MarketCap: $${big_data[setMarket][element_id]['marketcap']}</p><p>Change: 100%</p></div>`
    let info_window = $('#' + element_id)
    info_window.append(new_element);

}

function mouse_out() {
    let info_window = $('.info-window')
    info_window.remove();

}

function openModal(stock) {

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

            for (let tweet in data['tweets']['data']) {

                let tweets = `<li class="bg-light p-1 rounded twitter-element">
                <img class="d-inline" style="width:32px;height:32px;"src="resources/twitterTransparent.png">
                <b><a class="text-decoration-none text-primary" href="https://twitter.com/${data['tweets']['data'][tweet]['authorName']}">${data['tweets']['data'][tweet]['authorName']}</a></b>
                <a class="text-decoration-none text-dark" href="https://twitter.com/${data['tweets']['data'][tweet]['authorName']}/status/${data['tweets']['data'][tweet]['id']}"><p>${data['tweets']['data'][tweet]['text']}</p></a></li>`
                tweetArray.push(tweets)
            }
            let new_element = `<div id="info-overlay-window" class="overlay bg-dark">
                <div class="row">
                <div class="col-lg-6 bg-dark stock-window-element border border-light border-5"><h1 class="text-light text-center text-capitalize">${data['stock']['companyName']}</h1>
                <div class="col-lg-8 m-auto">
                <ul class="list-unstyled text-light pt-4 mt-3 text-center">
                    <li><h3 class="border border-light p-3">Price in USD: $${data['stock']['priceUSD']}</h3></li>
                    <li><h3 class="border border-light p-3">Price in SEK: ${data['stock']['priceSEK']}sek</h3></li>
                    <li><h3 class="border border-light p-3">Marketcap: $${marketcap}</h3></li>
                    
                    <li><h3 class="border border-light p-3">Change: ${data['stock']['percentChange']}%</h3></li>
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
        })
}

function close_overlay() {
    let element = $('#info-overlay-window')
    element.remove();

}





// Connection opened -> Subscribe
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
    for (let symbol in msg['data'])
        newText = `${msg['data'][symbol]['s']}:  ${msg['data'][symbol]['p']}`
    scrollText = scrollText + newText

    //$('#scroll-text').text(scrollText)

    //Update treemap elements

    for (let symbol in msg['data']) {
        lastPrice = { 'symbol': msg['data'][symbol]['s'], 'lastprice': msg['data'][symbol]['p'] }


        if (parseInt(lastPrice['lastprice']) > parseInt($(`#${msg['data'][symbol]['s']}price`).text())) {

            $(`#${lastPrice['symbol']}`).css("background-color", "green")


        } else {

            $(`#${lastPrice['symbol']}`).css("background-color", "red")

        }


        $(`#${lastPrice['symbol']}price`).html(`$${lastPrice['lastprice']}`)
    }

});



function unsubscribe() {
    
    if (sort_list.length > 1) {
        // Unsubscribe
        for (let symbol in sort_list) {
            socket.send(JSON.stringify({ 'type': 'unsubscribe', 'symbol': sort_list[symbol]['name'] }))
            
        }

        big_data = {}
        sort_list = []
    }
}



// TIME

function startTime() {
    const today = new Date();
    let h = today.getHours() - 6;
    let m = today.getMinutes();
    let s = today.getSeconds();
    m = checkTime(m);
    s = checkTime(s);
    document.getElementById('clock').innerHTML = h + ":" + m + ":" + s;
    let openHours = h + m
    if (openHours >= 900 && openHours < 1600) {
        $('#openClose').addClass("bg-success").html('Open')
        let hasClass = $('#openClose').hasClass("bg-danger")
        if (hasClass) {
            $('#openClose').removeClass("bg-danger")
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

