import React, {Component} from 'react';
import config from "../config";
import StockTable from "./StockTable";

class Stocks extends Component {

  constructor() {
    super();

    this.state = {
      stocks: []
    }
  }

  componentDidMount() {
    fetch(`${config.SERVER_URL}/stock`)
      .then(r => r.json())
      .then(json => this.setState({stocks: json}))
      .catch(e => console.warn(e))
  }


  render() {
    const {stocks} = this.state;


    return <StockTable stocks={stocks}/>
  }
}

export default Stocks;