import React, {Component} from 'react';
import config from "../config";

import {number,func,string}  from 'prop-types'
class Health extends Component {

    static propTypes = {
        waiterUp: number,
        stockUp: number,
        billingUp: number,
        render: func.isRequired,
    };

    state = {
        waiterUp:0,
        stockUp:0,
        billingUp:0
    }


    componentDidMount() {
        this.checkingBilling();
        this.checkStock1();
        this.checkingWaiter();
        setTimeout(function() { //Start the timer
            this.props.sendWaiter(this.state.waiterUp);
            this.props.sendBilling(this.state.billingUp);
            this.props.sendStock(this.state.stockUp);
        }.bind(this), 1000)
    }

    render() {
        const {waiterUp,billingUp,stockUp} = this.state;
        return (`Stock up ${stockUp==200?'Up':'Down'} Waiter: ${waiterUp==200?'Up':'Down'} Billing ${billingUp==200?'Up':'Down'} `)
    }

    updateStock(state) {
        this.setState({stockUp: state})
    }

    checkStock1() {
        console.log("Checking stock 1--------------------------------------")
        fetch(`${config.SERVER_URL}/stockStatus`).then((r) =>
            this.setState({stockUp: r.status})
        )
    }
    checkingWaiter() {
        fetch(`${config.SERVER_URL}/waiterStatus`).then((r) =>
            this.setState({waiterUp: r.status})
        )
    }

    checkingBilling() {
        fetch(`${config.SERVER_URL}/billingStatus`).then((r) =>
            this.setState({billingUp: r.status})
        )
    }
    stockUp(res) {
        this.setState({stockUp: res});
        console.log(" state "+res);
    }

}

export default Health;