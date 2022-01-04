let scrollText = ''

let big_data = {}
let sort_list = []
$(document).ready(function () {

    $.ajax({
        url: `http://127.0.0.1:8080/api/v1/symbols/tech`,
        headers: { "Accept": "application/json" }
    })
        .done(function (data) {
            
            big_data = data

            // Sort and order by market cap
            let total_cap = 0
            

            for (let symbol in data) {
                
                total_cap = total_cap + data[symbol]['marketcap'];
                sort_list.push({ 'name': data[symbol]['symbol'], 'marketcap': data[symbol]['marketcap']})
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
            
                //Creates element and appends to treemap element
                let text = `<div id="${sort_list[x]['name']}" class="treemap_element stock-element" style="width:${width}%; height:${height}px;" onmouseover="mouse_over(this.id)" onmouseout="mouse_out()" onclick="openModal(this.id)">
                <h3 class="text-capitalize">${sort_list[x]['name']}</h3><p id="${sort_list[x]['name']}price"></p><br></div>`
                
                
                $(text).appendTo("#treemap");
            }
        })
        
    
});



function mouse_over(element_id) {
    let procent = (Math.round(23 * 100) / 100).toFixed(2);
    
    let new_element = `<div class="info-window"><h3 class="text-capitalize">${element_id}</h3> <p class="text-capitalize">Name: ${element_id}</p> <p>MarketCap: $</p><p>Price: </p><p>Change: 100%</p></div>`



    let info_window = $('#' + element_id)
    info_window.append(new_element);

}

function mouse_out() {
    let info_window = $('.info-window')
    info_window.remove();

}

function openModal(stock) {

    $.ajax({
        url: `http://127.0.0.1:8080/api/v1/stocktweet/${stock}`,
        headers: { "Accept": "application/json" }
    })
        .done(function (data) {
            let marketcap = ''

            console.log(data)
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
                <div class="col-lg-6 bg-dark stock-window-element"><h1 class="text-light text-center text-capitalize">${data['stock']['companyName']}</h1>
                <div class="col-lg-8 m-auto">
                <ul class="list-unstyled text-light m-4">
                    <li><h3>Price in USD: ${data['stock']['priceUSD']}</h3></li>
                    <li><h3>Price in SEK: ${data['stock']['priceSEK']}</h3></li>
                    <li><h3>Marketcap: ${marketcap}</h3></li>
                    
                    <li><h3>Change: ${data['stock']['percentChange']}%</h3></li>
                    <li><h3>Symbol: ${data['stock']['symbol']}</h3></li>
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


const socket = new WebSocket('wss://ws.finnhub.io?token=c70ab7qad3id7ammkm3g');
// Connection opened -> Subscribe
socket.addEventListener('open', function (event) {
    for (let symbol in sort_list) {
        socket.send(JSON.stringify({ 'type': 'subscribe', 'symbol': sort_list[symbol]['name']}))
    }
});
// Listen for messages
socket.addEventListener('message', function (event) {

    //console.log('Message from server ', event.data);
    var msg = JSON.parse(event.data);
    //console.log(msg)
    let scrollText = ''
    for (let symbol in msg['data'])
        newText = msg['data'][symbol]['s'] + msg['data'][symbol]['p']
        scrollText = scrollText + newText

    $('#scroll-text').text(scrollText)

    //Update treemap elements

    for (let symbol in msg['data'])
        lastPrice = {'symbol': msg['data'][symbol]['s'], 'lastprice': msg['data'][symbol]['p']}
    

        
        if (lastPrice['lastprice'] > 0) {
            if (lastPrice['lastprice'] <= 500) {
                symbol = 'up_2'
            } else if (lastPrice['lastprice'] <= 1000) {
                symbol = 'up_5'
            } else {
                symbol = 'up_10'
            }

        } else {
            if (lastPrice['lastprice'] >= -2) {
                symbol = 'down_2'
            } else if (lastPrice['lastprice'] >= -5) {
                symbol = 'down_5'
            } else {
                symbol = 'down_10'
            }

        }

        $(`#${lastPrice['symbol']}`).addClass(symbol)
        $(`#${lastPrice['symbol']}price`).html(`$${lastPrice['lastprice']}`)

});
// Unsubscribe
var unsubscribe = function (symbol) {
    socket.send(JSON.stringify({ 'type': 'unsubscribe', 'symbol': symbol }))
}