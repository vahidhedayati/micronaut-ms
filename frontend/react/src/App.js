import React, {Component} from 'react';
import {BrowserRouter as Router, Route, Link} from "react-router-dom";
import logo from './images/logo.png'
import About from "./about";
import Stocks from "./stocks";
import Beer from "./beer";
import './App.css'


class App extends Component {
    constructor(props) {
        super(props);
        this.state = {
            customerName: ''
        }
    }
    render() {
        return (
            <Router>
                <div className="App">
                    <nav className="navbar navbar-expand-lg navbar-light bg-light">
                        <Link to="/" className="navbar-brand">
                          <img src={logo} className='micronaut-logo' alt='micronaut' /> Beer Shop</Link>
                        <button className="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarNav"
                                aria-controls="navbarNav" aria-expanded="false" aria-label="Toggle navigation">
                            <span className="navbar-toggler-icon"></span>
                        </button>
                        <div className="collapse navbar-collapse" id="navbarNav">
                            <ul className='navbar-nav'>
                                <li className='nav-item'>
                                    <Link to="/" className="nav-link">Home</Link>
                                </li>
                                <li className='nav-item'>
                                    <Link to="/stocks" className="nav-link">Stocks</Link>
                                </li>
                                <li className='nav-item'>
                                    <Link to="/beer" className="nav-link">Buy a Beer</Link>
                                </li>
                                <li className='nav-item'>
                                    <Link to="/payBill" className="nav-link">Pay Bill</Link>
                                </li>
                                <li className='nav-item'>
                                    <Link to="/about" className="nav-link">About</Link>
                                </li>
                            </ul>
                        </div>
                    </nav>

                    <div className="container">
                        <Route exact path="/stocks" component={Stocks} />
                        <Route exact path="/beer" component={Beer}/>
                        <Route exact path="/about" component={About}/>
                    </div>
                </div>
            </Router>
        );
    }
}

export default App;
