import React from 'react';
import StockRow from "./StockRow";
import {array} from 'prop-types'

const StockTable = ({stocks}) => <div>
{stocks.map((v) => <StockRow stock={v}/>)}
</div>

StockTable.propTypes = {
    stocks: array.isRequired
}

export default StockTable;