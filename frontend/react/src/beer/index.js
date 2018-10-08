import React, {Component} from 'react';
import config from "../config";
import StockTable from "./StockTable";
import Health from "../healthcheck";
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
            waiterUp:'',
            stockUp:'',
            billingUp:'',
            currentBill:{}


        }
        //checkStock2 = checkStock2.bind(this)
    }
    getWaiter(val){
        // do not forget to bind getData in constructor
        console.log("WE GOT THIS waiterUp"+ val);
        this.setState({waiterUp: val});
    }
    getBilling(val){
        // do not forget to bind getData in constructor
        console.log("WE GOT THIS billingUp"+ val);
        this.setState({billingUp: val});
    }
    getStock(val){
        // do not forget to bind getData in constructor
        console.log("WE GOT THIS stockUp"+ val);
        this.setState({stockUp: val});
    }


    serveBeer(customerName,beerType,amount,price,beerName) {
        fetch(`${config.SERVER_URL}/beer`, {
            method: 'POST',
            body: JSON.stringify({customerName:customerName, beerName:beerName, beerType: beerType,amount:amount,price:price}),
            headers: {'Content-Type': 'application/json'}
        }).then((r) => {
            r.status === 200 ?
            this.addBeer(beerType,beerName,amount,customerName):
            this.setState({bought: false})
    }).then((json) => console.log(json))
    .catch(e => console.warn(e));

    }
    renderSwitch(beerType) {
        switch(beerType) {
            case 'PINT':
                return "pints";
            case 'HALF_PINT':
                return "halfPints";
            default:
                return "bottles";
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
        fetch(`${config.SERVER_URL}/bill/${customerName}`)
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
    //const {stocks} = this.state;

      const updateName = this.updateName.bind(this);
      const updateAmount = this.updateAmount.bind(this);
      const buy = this.buy.bind(this);

      const logout = this.logout.bind(this);
      // const handleNameChange= this.handleNameChange.bind(this);
      //const cc = checkStock2.bind(this);
      const {customerName,stocks,amount,bought,active,waiterUp,stockUp,billingUp,currentBill} = this.state;

    const getWaiter = this.getWaiter.bind(this);
    const getBilling = this.getBilling.bind(this);
    const getStock = this.getStock.bind(this);
      const  loadBill= this.loadBill.bind(this);

      function loadBar(loadBill,customerName,getWaiter,getBilling,getStock) {
          loadBill(customerName)
          return (<Row>
              <Col >
              <Health sendWaiter={getWaiter} sendBilling={getBilling}  sendStock={getStock} />
              </Col>
              <Col>
              <StockTable stocks={stocks} customerName={customerName} logout={logout} amount={amount}
              updateAmount={updateAmount} buy={buy} active={active} waiterUp={waiterUp} stockUp={stockUp} billingUp={billingUp}
          currentBill={currentBill} />
          </Col>
          </Row>
          )
      }
      function loadUserForm(getWaiter,getBilling,getStock) {
          return (
              <Row>
            <h2>Provide a name ! </h2>
              <Row>
              <Col >
                <input type="text" defaultValue={customerName} name="customerName" onBlur={ updateName }/>
              </Col>

              </Row>
              <Row>
              <Col>
                    <br/><br/><Health sendWaiter={getWaiter} sendBilling={getBilling}  sendStock={getStock} />
              </Col>
              </Row>
          </Row>
          )
      }

    return (customerName ?  loadBar(loadBill,customerName,getWaiter,getBilling,getStock) : loadUserForm(getWaiter,getBilling,getStock))
  }
}

export default Beer;