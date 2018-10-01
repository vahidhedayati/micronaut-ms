    package micronaut.demo.beer.service;

    import io.micronaut.context.annotation.Value;
    import micronaut.demo.beer.model.BeerItem;
    import micronaut.demo.beer.model.Ticket;

    public class BeerCostCalculator implements CostCalculator {

        //Default value added of 5
        @Value("${beer.base.cost.value:5}")
        private int beerBaseCost =1;

        //VAT default value of 17.5 added
        @Value("${vat.value:17}")
        private int vat ;

        public int getVat() {
            return vat;
        }

        public void setVat(int c) {
            this.vat = c;
        }

        public int getBeerBaseCost() {
            return this.beerBaseCost;
        }

        public void setBeerBaseCost(int c) {
            this.beerBaseCost = c;
        }

        public double calculateCost(Ticket ticket) {
            double costNoVat = allBeersCost(ticket);
            double costVat = costNoVat*vat/100;
            return costNoVat+costVat;
        }

        private double allBeersCost(Ticket ticket) {
            return ticket
                    .getBeerItems()
                    .stream()
                    .map( beer ->  calculateBeerCost(beer))
                    .mapToDouble(i->i).sum();
        }

        private double calculateBeerCost(BeerItem beer) {

            switch (beer.getSize()) {
                case SMALL : return 1* beerBaseCost;
                case MEDIUM: return 2* beerBaseCost;
                case PINT: return 3* beerBaseCost;
                case EMPTY: return beerBaseCost;
                default: return 99;
            }
        }
    }
