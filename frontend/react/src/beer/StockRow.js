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
function showBottle(stock,buy,active,billingUp,waiterUp,amount) {
    return (<p className="card-text">
            Bottles Available: {stock.bottles} -
            Per Bottle cost: {stock.bottleCost}
    {(waiterUp === 200 ?
        (billingUp===200 ?
            (amount < stock.bottles ?
        <input type="text"   className={active ===stock.name+"_BOTTLE"? 'btn btn-danger readonly': "btn btn-primary readonly"}
        name="beerType" data-bname={stock.name} data-price={stock.bottleCost} defaultValue="BOTTLE" onClick={buy}/>
        : <span className='btn btn-danger'>Out of stock</span>)
    : <span className='btn btn-danger'>Billing system is offline</span>)
    : <span className='btn btn-danger'>Waiter is busy or not around - please try again</span>)}

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
function showPints(stock,buy,active,billingUp,waiterUp,amount) {
    return (
        <p className="card-text">
        Pints Available: {stock.availablePints} -
        Pint cost:  {stock.pintCost}
    {(waiterUp === 200 ?
        (billingUp===200 ?
            (amount < stock.availablePints ?
            <input type="text"  className={(active ===stock.name+"_PINT") ? 'btn btn-danger readonly': 'btn btn-primary readonly'}
            name="beerType"  data-bname={stock.name}  data-price={stock.pintCost} defaultValue="PINT" onClick={buy}/>
    : <span className='btn btn-danger'>Out of stock</span>)
    : <span className='btn btn-danger'>Billing system is offline</span>)
    : <span className='btn btn-danger'>Waiter is busy or not around - please try again</span>)}
    <br/>
        Half Pint cost:  {stock.halfPintCost}
        {(waiterUp === 200 ?
            (billingUp===200 ?
                (amount < stock.availablePints*2 ?
            <input type="text" className={active ===stock.name+"_HALF_PINT" ? 'btn btn-danger readonly': 'btn btn-primary readonly'}
             name="beerType"  data-bname={stock.name} data-price={stock.halfPintCost} defaultValue="HALF_PINT" onClick={buy}/>
        : <span className='btn btn-danger'>Out of stock</span>)
        : <span className='btn btn-danger'>Billing system is offline</span>)
        : <span className='btn btn-danger'>Waiter is busy or not around - please try again</span>)}
        </p>
    )
}
const StockRow = ({stock, customerName, amount, updateAmount,buy,active,stockUp,billingUp,waiterUp}) => <div className="card vendor-card">


  <div className="card-body">
    <h5 className="card-title">{stock.name}</h5>

    { (stock.bottles > 1 || stock.availablePints > 1 ) ?
        amount ? amount : amountForm(amount, updateAmount)
        : '' }
    { stock.bottles > 1 ?  showBottle(stock,buy,active,billingUp,waiterUp,amount) : noBottles() }
    { stock.availablePints > 1 ?  showPints(stock,buy,active,billingUp,waiterUp,amount) : noPints() }
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