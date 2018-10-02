import React from 'react'
import {shape, number,  string}  from 'prop-types'

const StockRow = ({stock}) => <div className="card vendor-card">

  <div className="card-body">
    <h5 className="card-title">{stock.name}</h5>
    <p className="card-text">
        Bottles Available: {stock.bottles}
        (Barrels: {stock.barrels} - Pints Available: {stock.availablePints})
        Bottle cost: (Customer: {stock.bottleCost}) (WholeSale:{stock.baseBottleCost})
        Pint cost: (Customer: {stock.pintCost}) (WholeSale: {stock.basePintCost})
        Half Pint cost: (Customer: {stock.halfPintCost}) (WholeSale: {stock.baseHalfPintCost})
</p>
  </div>
</div>

StockRow.propTypes = {
    stock: shape({
    name: string,
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