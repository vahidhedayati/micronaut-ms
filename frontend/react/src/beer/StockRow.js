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
function showBottle(stock,buy,active,billingUp,waiterUp) {
    return (<p className="card-text">
            Bottles Available: {stock.bottles} -
            Per Bottle cost: {stock.bottleCost}
    {billingUp==200 ? <input type="text" className={active ===stock.name+"_BOTTLE"? 'btn btn-danger readonly': "btn btn-primary readonly"} name="beerType" data-bname={stock.name} data-price={stock.bottleCost} value="BOTTLE" onClick={buy}/> : ""}

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
function showPints(stock,buy,active,billingUp,waiterUp) {
    return (
        <p className="card-text">
        Pints Available: {stock.availablePints} -
        Pint cost:  {stock.pintCost}
    {billingUp==200 ?  <input type="text" className={(active ===stock.name+"_PINT") ? 'btn btn-danger readonly': 'btn btn-primary readonly'} name="beerType"  data-bname={stock.name}  data-price={stock.pintCost} value="PINT" onClick={buy}/> : "" }
    <br/>
        Half Pint cost:  {stock.halfPintCost}

        {billingUp==200 ?  <input type="text" className={active ===stock.name+"_HALF_PINT" ? 'btn btn-danger readonly': 'btn btn-primary readonly'} name="beerType"  data-bname={stock.name} data-price={stock.halfPintCost} value="HALF_PINT" onClick={buy}/> : "" }
        </p>
    )
}
const StockRow = ({stock, customerName, amount, updateAmount,buy,active,stockUp,billingUp,waiterUp}) => <div className="card vendor-card">


  <div className="card-body">
    <h5 className="card-title">{stock.name}</h5>

    { (stock.bottles > 1 || stock.availablePints > 1 ) ?
        amount ? amount : amountForm(amount, updateAmount)
        : '' }
    { stock.bottles > 1 ?  showBottle(stock,buy,active,billingUp,waiterUp) : noBottles() }
    { stock.availablePints > 1 ?  showPints(stock,buy,active,billingUp,waiterUp) : noPints() }
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