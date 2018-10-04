import React from 'react';
import StockRow from "./StockRow";
import {array} from 'prop-types'



const StockTable = ({stocks,customerName,logout, amount, updateAmount,buy}) => <div>
 <h2>Welcome {customerName} now buy a beer if any available</h2>


<button onClick={logout} name="logout">logout</button>


{stocks.map((v) => <StockRow  stock={v} customerName={customerName}  amount={amount} updateAmount={updateAmount} buy={buy} />)}
</div>

StockTable.propTypes = {
    stocks: array.isRequired
}

export default StockTable;