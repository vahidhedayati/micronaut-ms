import React, {Component} from 'react';
import config from "../config";
import StockTable from "./StockTable";
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
            beerName:''
        }
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

      const {customerName,stocks,amount,bought} = this.state;

      function loadBar() {
          return (
              <StockTable stocks={stocks} customerName={customerName} logout={logout} amount={amount} updateAmount={updateAmount} buy={buy} />


          )
      }
      function loadUserForm() {
          return (
              <Row>

               <h1>Provide a name !</h1>


              <Row>
              <Col >
                <input type="text" defaultValue={customerName} name="customerName" onBlur={ updateName }/>
              </Col>

              </Row>
          </Row>
          )
      }

    return (customerName ?loadBar() : loadUserForm())
  }
}

export default Beer;