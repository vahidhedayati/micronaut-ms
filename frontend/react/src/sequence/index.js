import React, {Component} from 'react';
import config from "../config";

import {Row, Col} from 'react-bootstrap';

class Sequence extends Component {
    constructor(props) {
        super(props);
        this.state = {
            stocks: [],


        }
        //checkStock2 = checkStock2.bind(this)
    }


    testSequence(event) {
        fetch(`${config.SERVER_URL}/testSequence`)
            .then(r => r.json())
            .then(json => console.log(JSON.stringify(json)))
            .catch(e => console.warn(e));
            //Reload the stocks again
            this.loadSequences();


    }


    loadSequences() {
        fetch(`${config.SERVER_URL}/sequence`)
            .then(r => r.json())
            .then(json => console.log(JSON.stringify(json)))
            .catch(e => console.warn(e))
    }
    componentDidMount() {
       this.loadSequences();
    }


  render() {
      const testSequence = this.testSequence.bind(this);
      const {stocks} = this.state;


      function loadUserForm(stocks,testSequence) {
          return (
              <Row>
            <h2>{stocks} </h2>
              <Row>
              <Col >

          <input type="text"   className="btn btn-xs btn-primary readonly"
          name="beerType"  defaultValue="Test Sequence" onClick={testSequence}/>
              </Col>

              </Row>
              <Row>
              </Row>
          </Row>
          )
      }

    return (loadUserForm(stocks,testSequence))
  }
}

export default Sequence;