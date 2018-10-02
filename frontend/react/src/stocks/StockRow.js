import React from 'react'
import {shape,  string}  from 'prop-types'

const StockRow = ({stock}) => <div className="card vendor-card">

  <div className="card-body">
    <h5 className="card-title">{stock.name}</h5>
    <p className="card-text">{stock.bottles} {stock.availablePints} {stock.bottleCost} {stock.pintCost}</p>
  </div>
</div>

StockRow.propTypes = {
    stock: shape({
    name: string,
    bottles: string,
        availablePints: string,
        bottleCost: string,
        pintCost: string

  })
}

export default StockRow