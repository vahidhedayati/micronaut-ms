import React, {Component} from 'react';
import config from "../config";
import StockTable from "./StockTable";
import {Row, Col} from 'react-bootstrap';

class Beer extends Component {
    constructor(props) {
        super(props);
        this.state = {
            customerName: localStorage.getItem('customerName') || '',
            stocks: [],
            amount:'',
            beerType:'',
            bought:'',
            price:'',
            beerName:'',
            active:'',
            tabUp:'',
            currentBill:{}


        }
        //checkStock2 = checkStock2.bind(this)
    }

    serveBeer(customerName,beerType,amount,price,beerName) {
        fetch(`${config.SERVER_URL}/beer2`, {
            method: 'POST',
            body: JSON.stringify({customerName:customerName, beerName:beerName, beerType: beerType,amount:amount,price:price}),
            headers: {'Content-Type': 'application/json'}
        }).then((r) => {
            r.status === 200 ?
            this.addBeer(beerType,beerName,amount,customerName):
            this.setState({bought: false})
    }).then((json) => console.log(json))
    .catch(e => this.addBeer(beerType,beerName,amount,customerName));


    }
    renderSwitch(beerType) {
        switch(beerType) {
            case 'PINT':
                return "pints2";
            case 'HALF_PINT':
                return "halfPints2";
            default:
                return "bottles2";
        }
    }
    addBeer(beerType,beerName,amount,customerName) {
        var url="";
        fetch(`${config.SERVER_URL}/${this.renderSwitch(beerType)}`, {
            method: 'POST',
            body: JSON.stringify({name:beerName, amount:amount}),
            headers: {'Content-Type': 'application/json'}
        }).then((r) => {
            r.status === 200 ?
            this.loadBill(customerName,true):
            this.setState({bought: false})
    }).then((json) => console.log(json))
    .catch(e => console.warn(e));

    }

    loadBill(customerName,reload) {
        fetch(`${config.SERVER_URL}/bill2/${customerName}`)
            .then(r => r.json())
            .then(json => this.setState({currentBill: json}))
            .catch(e => console.warn(e));
        if (reload) {
            //Reload the stocks again
            this.loadStocks();
        }

    }
    logout(event) {
        this.setState({ customerName:"" });
        localStorage.setItem('customerName', "")

    }
    buy(event) {
        //this.toggleClass(event.target.attributes['data-bname'].value);
        this.setState({ active: event.target.attributes['data-bname'].value+"_"+event.target.value});
        console.log("--- "+event.target.value+" "+event.target.attributes['data-price'].value)
        this.setState({ beerType: event.target.value });
        //console.log(" > "+`${config.SERVER_URL}/beer/${customerName}/${beerType}/${amount}`)
        //fetch(config.SERVER_URL+'/beer/'+customerName+"/"+beerType+"/"+amount);
        this.serveBeer(this.state.customerName,event.target.value,this.state.amount,event.target.attributes['data-price'].value,event.target.attributes['data-bname'].value);
    }

    updateAmount(event) {
        this.setState({ amount: event.target.value });
    }
    updateName(event) {
        this.setState({ customerName: event.target.value })

            localStorage.setItem('customerName', event.target.value)
        this.loadBill(event.target.value);
    }

    loadStocks() {
        fetch(`${config.SERVER_URL}/stock`)
            .then(r => r.json())
            .then(json => this.setState({stocks: json}))
            .catch(e => console.warn(e))
    }
    componentDidMount() {
       this.loadStocks();
    }


  render() {
      const updateName = this.updateName.bind(this);
      const updateAmount = this.updateAmount.bind(this);
      const buy = this.buy.bind(this);
      const logout = this.logout.bind(this);
      const {customerName,stocks,amount,active,currentBill} = this.state;
      const  loadBill= this.loadBill.bind(this);

      function loadBar(currentBill,loadBill,customerName) {
          console.log('loading bill'+currentBill.cost);
          if (currentBill.cost==undefined) {
              console.log('loading bill');
              loadBill(customerName)
          }

          return (<Row>
              <Col>
              <StockTable stocks={stocks} customerName={customerName} logout={logout} amount={amount}
              updateAmount={updateAmount} buy={buy} active={active} currentBill={currentBill} />
          </Col>
          </Row>
          )
      }
      function loadUserForm() {
          return (
              <Row>
            <h2>Provide a name ! </h2>
              <Row>
              <Col >
                <input type="text" defaultValue={customerName} name="customerName" onBlur={ updateName }/>
              </Col>

              </Row>
              <Row>
              </Row>
          </Row>
          )
      }

    return (customerName ?  loadBar(currentBill,loadBill,customerName) : loadUserForm())
  }
}

export default Beer;