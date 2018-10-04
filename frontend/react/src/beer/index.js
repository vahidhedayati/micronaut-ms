import React, {Component} from 'react';
import config from "../config";
import StockTable from "./StockTable";
import {Table,Row, Col} from 'react-bootstrap';

class Beer extends Component {

  constructor() {
    super();

    this.state = {
      customerName: '',
        stocks: [],
        amount:''
    }
  }
    logout(event) {
        this.setState({ customerName:"" });
    }
    updateAmount(event) {
        this.setState({ amount: event.target.value });
    }
    updateName(event) {
        this.setState({ customerName: event.target.value })
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

      const logout = this.logout.bind(this);
      // const handleNameChange= this.handleNameChange.bind(this);

      const {customerName,stocks,amount} = this.state;

      function loadBar() {
          return (
              <StockTable stocks={stocks} customerName={customerName} logout={logout} amount={amount} updateAmount={updateAmount} />
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