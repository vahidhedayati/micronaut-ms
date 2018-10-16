import React from 'react';
import StockRow from "./StockRow";
import {array} from 'prop-types'
//import { library } from '@fortawesome/fontawesome-svg-core';
import { faUser, faSignOutAlt,faMoneyBill,faServer,faClone } from '@fortawesome/free-solid-svg-icons';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

const StockTable = ({stocks,customerName,logout, amount, updateAmount,buy,active,stockUp,billingUp,waiterUp,currentBill,tabUp,tab}) => <div>
<nav className="navbar navbar-expand-lg navbar-light bg-light">

    <ul className="navbar-nav mr-auto mt-2 mt-lg-0">

    <li className="nav-item">
        <FontAwesomeIcon color="#6DB65B" icon={faUser} /> {customerName}
    </li>
<li className="nav-item">
    <FontAwesomeIcon color="blue" icon={faMoneyBill} /> {Number(currentBill.cost).toFixed(2)}

    <FontAwesomeIcon color="orange" icon={faServer} /> {currentBill.deskId}
    <FontAwesomeIcon color="cyan" icon={faClone} /> {currentBill.waiterId}
</li>

</ul>
<span className="form-inline my-2 my-lg-0">
    <button className="link" onClick={logout} name="logout"><FontAwesomeIcon  color="red"  icon={faSignOutAlt}/>
logout</button>
</span>
</nav>



{stocks.map(v => <StockRow  key={v.name} stock={v} customerName={customerName}  amount={amount} updateAmount={updateAmount} buy={buy} active={active} waiterUp={waiterUp}
stockUp={stockUp} billingUp={billingUp} tabUp={tabUp} tab={tab}/>)}

</div>

StockTable.propTypes = {
    stocks: array.isRequired
}

export default StockTable;