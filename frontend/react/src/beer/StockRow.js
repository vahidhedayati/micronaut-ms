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
function showBottle(stock,buy,active,billingUp,waiterUp,amount,tab) {
    return (<p className="card-text">
            Bottles Available: {stock.bottles} -
            Per Bottle cost: { Number(stock.bottleCost).toFixed(2)} (exc vat)
<hr/>
            <div className="btn btn-xs btn-success ">
        Waiter decides:<br/>
<input type="text"   className={active ===stock.name+"_BOTTLE"? 'btn btn-xs btn-danger readonly': "btn btn-xs btn-primary readonly"}
    name="beerType" data-bname={stock.name} data-price={stock.bottleCost} defaultValue="BOTTLE" onClick={buy}/>
    </div>
    <hr/>
    <div className="btn btn-xs btn-warning ">
        Nested checks decide on tab/billing<br/>
        {loadDynamic('BOTTLE',stock.bottleCost,amount,waiterUp,billingUp,stock,buy,tab,active) }
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


function showPints(stock,buy,active,billingUp,waiterUp,amount,tab) {
    return (
        <p className="card-text">
        Pints Available: {stock.availablePints} -
        Pint cost:  {Number(stock.pintCost).toFixed(2)} (exc vat)
        {loadDynamic('PINT',stock.pintCost,amount,waiterUp,billingUp,stock,buy,tab,active) }
        <br/>
        Half Pint cost:  {Number(stock.halfPintCost).toFixed(2)} (exc vat)
        {loadDynamic('HALF_PINT',stock.halfPintCost,amount,waiterUp,billingUp,stock,buy,tab,active) }
        </p>
    )
}

function loadDynamic(currentValue,cost,amount,waiterUp,billingUp,stock,buy,tab,active) {
    return (
        <span>
        {amount ?
                (waiterUp === 200 ?
                    (billingUp===200 ?
                        (amount < stock.bottles ?
                        <input type="text"   className={active ===stock.name+"_"+currentValue? 'btn btn-xs btn-danger readonly': "btn btn-xs btn-primary readonly"}
    name="beerType" data-bname={stock.name} data-price={cost} defaultValue={currentValue} onClick={buy}/>
:<span className='btn btn-xs btn-danger'>Out of stock</span>
)
:(<input type="text"   className={active ===stock.name+"_"+currentValue? 'btn btn-xs btn-danger readonly': "btn btn-xs btn-success readonly"}
    name="beerType" data-bname={stock.name} data-price={cost} defaultValue={currentValue} onClick={tab}/>
)
)
:<span className='btn  btn-xs btn-danger'>Waiter is busy or not around - please try again</span>
)
:<span className='btn  btn-xs btn-danger'>Type in numeric amount required</span>
}
</span>
)

}
const StockRow = ({stock, customerName, amount, updateAmount,buy,active,stockUp,billingUp,waiterUp,tab}) => <div className="card vendor-card">


  <div className="card-body">
    <h5 className="card-title">{stock.name}</h5>

    { (stock.bottles > 1 || stock.availablePints > 1 ) ?
        amount ? amount : amountForm(amount, updateAmount)
        : '' }
    { stock.bottles > 1 ?  showBottle(stock,buy,active,billingUp,waiterUp,amount,tab) : noBottles() }
    { stock.availablePints > 1 ?  showPints(stock,buy,active,billingUp,waiterUp,amount,tab) : noPints() }
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
