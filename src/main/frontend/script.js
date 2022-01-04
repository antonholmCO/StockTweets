let scrollText = ''


const socket = new WebSocket('wss://ws.finnhub.io?token=c70ab7qad3id7ammkm3g');
// Connection opened -> Subscribe
socket.addEventListener('open', function (event) {
    socket.send(JSON.stringify({ 'type': 'subscribe', 'symbol': 'BINANCE:BTCUSDT' }))
    socket.send(JSON.stringify({ 'type': 'subscribe', 'symbol': 'IC MARKETS:1' }))
    
});
// Listen for messages
socket.addEventListener('message', function (event) {
    
    //console.log('Message from server ', event.data);
    var msg = JSON.parse(event.data);
    let scrollText = ''
    for (let symbol in msg['data'])
        newText = msg['data'][symbol]['s'] + msg['data'][symbol]['p']
        scrollText = scrollText + newText
    
    $('#scroll-text').text(scrollText)
    
});
// Unsubscribe
var unsubscribe = function (symbol) {
    socket.send(JSON.stringify({ 'type': 'unsubscribe', 'symbol': symbol }))
}


let big_data = {}

$(document).ready(function () {

    $.ajax({
        url: 'https://api.coingecko.com/api/v3/simple/price?ids=bitcoin%2Cripple%2Cstellar%2Cdecentraland%2Csolana%2Ccardano%2Crevain%2Cethereum%2Ctether&vs_currencies=usd&include_market_cap=true&include_24hr_change=true',
        headers: { "Accept": "application/json" }
    })
        .done(function (data) {

            big_data = data

            // Sort and order by market cap
            let total_cap = 0
            let sort_list = []

            for (let f in data) {
                total_cap = total_cap + data[f].usd_market_cap;
                sort_list.push({ 'name': f, 'marketcap': data[f].usd_market_cap, 'change': data[f].usd_24h_change, 'price': data[f].usd })
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
            for (let x in sort_list) {

                //Convert change to %
                let procent = (Math.round(sort_list[x].change * 100) / 100).toFixed(2);

                //Calculate percentage of this list marketcap
                let market_share = sort_list[x].marketcap / total_cap
                let market_percentage = (Math.round(market_share * 100)).toFixed(2);

                //Width is based on market share
                let width = market_percentage
                let height = 150
                let symbol = ''

                // Sets class on element based on stockprice increased or decreased
                if (procent > 0) {
                    if (procent <= 2) {
                        symbol = 'up_2'
                    } else if (procent <= 5) {
                        symbol = 'up_5'
                    } else {
                        symbol = 'up_10'
                    }

                } else {
                    if (procent >= -2) {
                        symbol = 'down_2'
                    } else if (procent >= -5) {
                        symbol = 'down_5'
                    } else {
                        symbol = 'down_10'
                    }

                }

                //Creates element and appends to treemap element
                let text = `<div id="${sort_list[x].name}" class="${symbol} stock-element" style="width:${width}%; height:${height}px;" onmouseover="mouse_over(this.id)" onmouseout="mouse_out()" onclick="openModal(this.id)"><h3 class="text-capitalize">${sort_list[x].name}</h3><p>$${sort_list[x].price}<strong><br>${procent}</strong>%<br><em>Size, width ${market_percentage}%</p></div>`
                $(text).appendTo("#treemap");
            }
        })
        
    
});



function mouse_over(element_id) {
    let procent = (Math.round(big_data[element_id].usd_24h_change * 100) / 100).toFixed(2);
    let new_element = `<div class="info-window"><h3 class="text-capitalize">${element_id}</h3> <p class="text-capitalize">Name: ${element_id}</p> <p>MarketCap: $${big_data[element_id].usd_market_cap}</p><p>Price: ${big_data[element_id].usd}</p><p>Change: ${procent}%</p></div>`



    let info_window = $('#' + element_id)
    info_window.append(new_element);

}

function mouse_out() {
    let info_window = $('.info-window')
    info_window.remove();

}

function openModal(stock) {

    $.ajax({
        url: `http://127.0.0.1:8080/api/v1/tweets/${stock}`,
        headers: { "Accept": "application/json" }
    })
        .done(function (data) {
            console.log(big_data[stock])
            

            let tweetArray = []

            for (let tweet in data) {
                let tweets = `<li class="bg-light p-1 rounded twitter-element"><img class="d-inline" style="width:32px;height:32px;"src="resources/twitterTransparent.png"><a href="#" class="text-primary text-decoration-none">${data[tweet]['id']}</a><p>${data[tweet]['text']}</p></li>`
                tweetArray.push(tweets)
            }
            let new_element = `<div id="info-overlay-window" class="overlay bg-dark">
                <div class="row">
                <div class="col-lg-6 bg-dark stock-window-element"><h1 class="text-light text-center text-capitalize">${stock}</h1>
                <div class="col-lg-8 m-auto">
                <ul class="list-unstyled text-light m-4">
                    <li><h3>Price: ${big_data[stock]['usd']}</h3></li>
                    <li><h3>Marketcap: ${big_data[stock]['usd_market_cap']}</h3></li>
                    
                    <li><h3>Change 24h: ${(big_data[stock]['usd_24h_change'] * 100 / 100).toFixed(2)}%</h3></li>
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


