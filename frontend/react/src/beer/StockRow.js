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
function showBottle(stock,buy) {
    return (<p className="card-text">
            Bottles Available: {stock.bottles} -
            Per Bottle cost: {stock.bottleCost}  <input type="text" className="btn btn-primary readonly" name="beerType" data-bname={stock.name} data-price={stock.bottleCost} value="BOTTLE" onClick={buy}/>
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
function showPints(stock,buy) {
    return (
        <p className="card-text">
        Pints Available: {stock.availablePints} -
        Pint cost:  {stock.pintCost}  <input type="text" className="btn btn-primary readonly" name="beerType" data-price={stock.pintCost} value="PINT" onClick={buy}/> <br/>
        Half Pint cost:  {stock.halfPintCost} <input type="text" className="btn btn-primary readonly" name="beerType"  data-bname={stock.name} data-price={stock.halfPintCost} value="HALF_PINT" onClick={buy}/>
        </p>
    )
}
const StockRow = ({stock, customerName, amount, updateAmount,buy}) => <div className="card vendor-card">


  <div className="card-body">
    <h5 className="card-title">{stock.name}</h5>
    { (stock.bottles > 1 || stock.availablePints > 1 ) ?
        amount ? amount : amountForm(amount, updateAmount)
        : '' }
    { stock.bottles > 1 ?  showBottle(stock,buy) : noBottles() }
    { stock.availablePints > 1 ?  showPints(stock,buy) : noPints() }
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