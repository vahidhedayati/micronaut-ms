import React from 'react'
import {shape, number,  string}  from 'prop-types'

function amountForm(amount,updateAmount) {
    return (
        <p className="card-text">
            Amount required <input type="text" name="amount" defaultValue={amount} onBlur={updateAmount}/>
        </p>
    )
}
function noBottles() {
    return (
        <p className="card-text">
        No bottles for sale at the moment try again later
        </p>
    )
}
function showBottle(stock,buy,active) {
    return (<p className="card-text">
            Bottles Available: {stock.bottles} -
            Per Bottle cost: { Number(stock.bottleCost).toFixed(2)} (exc vat)
    <div>
<input type="text"   className={active ===stock.name+"_BOTTLE"? 'btn btn-xs btn-danger readonly': "btn btn-xs btn-primary readonly"}
    name="beerType" data-bname={stock.name} data-price={stock.bottleCost} defaultValue="BOTTLE" onClick={buy}/>
    </div>

    </p>
    )
}

function noPints() {
    return (
        <p className="card-text">
            No Pints for sale at the moment try again later
        </p>
    )
}


function showPints(stock,buy,active) {
    return (
        <p className="card-text">
        Pints Available: {stock.availablePints} -
        Pint cost:  {Number(stock.pintCost).toFixed(2)} (exc vat)
<input type="text"   className={active ===stock.name+"_PINT"? 'btn btn-xs btn-danger readonly': "btn btn-xs btn-primary readonly"}
    name="beerType" data-bname={stock.name} data-price={stock.pintCost} defaultValue="PINT" onClick={buy}/>

    <br/>
        Half Pint cost:  {Number(stock.halfPintCost).toFixed(2)} (exc vat)
<input type="text"   className={active ===stock.name+"_HALF_PINT"? 'btn btn-xs btn-danger readonly': "btn btn-xs btn-primary readonly"}
    name="beerType" data-bname={stock.name} data-price={stock.bottleCost} defaultValue="HALF_PINT" onClick={buy}/>

    </p>
    )
}


const StockRow = ({stock, customerName, amount, updateAmount,buy,active}) => <div className="card vendor-card">


  <div className="card-body">
    <h5 className="card-title">{stock.name}</h5>

    { (stock.bottles > 1 || stock.availablePints > 1 ) ?
        amount ? amount : amountForm(amount, updateAmount,active)
        : '' }
    { stock.bottles > 1 ?  showBottle(stock,buy) : noBottles() }
    { stock.availablePints > 1 ?  showPints(stock,buy,active) : noPints() }
  </div>

</div>

StockRow.propTypes = {
    stock: shape({
    name: string,
        markupName:string,
    bottles: number,
        availablePints: number,
        barrels: number,
        bottleCost: number,
        baseBottleCost: number,
        pintCost: number,
        basePintCost: number,
        halfPintCost: number,
        baseHalfPintCost:number

  })
}

export default StockRow
