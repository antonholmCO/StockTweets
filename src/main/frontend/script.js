
$(document).ready(function () {

    $.ajax({
        url: 'https://api.coingecko.com/api/v3/simple/price?ids=bitcoin%2Cripple%2Cstellar%2Cdecentraland%2Csolana%2Ccardano%2Crevain%2Cethereum%2Ctether&vs_currencies=usd&include_market_cap=true&include_24hr_change=true',
        headers: { "Accept": "application/json" }
    })
    .done(function (data) {


        // Sort and order by market cap
        let total_cap = 0
        let sort_list = []

        for (let f in data) {
            total_cap = total_cap + data[f].usd_market_cap;
            sort_list.push({ 'name': f, 'marketcap': data[f].usd_market_cap, 'change': data[f].usd_24h_change, 'price': data[f].usd})
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
            if (sort_list[x].change > 0) {
                symbol = 'up'
            } else {
                symbol = 'down'
            }

            //Creates element and appends to treemap element
            let text = `<div class="${symbol}" style="width:${width}%; height:${height}px;"><h3>${sort_list[x].name}</h3>$${sort_list[x].price}<strong><br>${procent}</strong>%<br><em>Size, width ${market_percentage}%</div>`
            $(text).appendTo("#treemap");
        }
    })
});