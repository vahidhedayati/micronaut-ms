import React, {Component} from 'react';
import config from "../config";
import StockTable from "./StockTable";
import Health from "../healthcheck";
import {Table,Row, Col} from 'react-bootstrap';

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
            billingUp:''

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
            this.setState({bought: true}) :
            this.setState({bought: false})
    }).then((json) => console.log(json))
    .catch(e => console.warn(e));

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

    }

    componentDidMount() {
        fetch(`${config.SERVER_URL}/stock`)
      .then(r => r.json())
      .then(json => this.setState({stocks: json}))
      .catch(e => console.warn(e))
    }


  render() {
    //const {stocks} = this.state;

      const updateName = this.updateName.bind(this);
      const updateAmount = this.updateAmount.bind(this);
      const buy = this.buy.bind(this);

      const logout = this.logout.bind(this);
      // const handleNameChange= this.handleNameChange.bind(this);
      //const cc = checkStock2.bind(this);
      const {customerName,stocks,amount,bought,active,waiterUp,stockUp,billingUp} = this.state;

    const getWaiter = this.getWaiter.bind(this);
    const getBilling = this.getBilling.bind(this);
    const getStock = this.getStock.bind(this);


      function loadBar(getWaiter,getBilling,getStock) {
          return (<Row>
              <Col >
              <Health sendWaiter={getWaiter} sendBilling={getBilling}  sendStock={getStock} />
              </Col>
              <Col>
              <StockTable stocks={stocks} customerName={customerName} logout={logout} amount={amount}
              updateAmount={updateAmount} buy={buy} active={active} waiterUp={waiterUp} stockUp={stockUp} billingUp={billingUp} />
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

    return (customerName ?loadBar(getWaiter,getBilling,getStock) : loadUserForm(getWaiter,getBilling,getStock))
  }
}

export default Beer;