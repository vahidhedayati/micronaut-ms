/*
 * Copyright 2018 original authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package micronaut.demo.beer


import io.micronaut.context.annotation.Parameter
import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Produces
import io.reactivex.Maybe
import io.reactivex.Single
import micronaut.demo.beer.client.MarkupControllerClient
import micronaut.demo.beer.client.StockControllerClient
import micronaut.demo.beer.client.WaiterControllerClient
import micronaut.demo.beer.model.Beer

@Controller("/")
class BeerController {


    private final StockControllerClient stockControllerClient
    private final WaiterControllerClient waiterControllerClient
    private final MarkupControllerClient markupControllerClient

    BeerController(
            StockControllerClient stockControllerClient,
            WaiterControllerClient waiterControllerClient,
            MarkupControllerClient markupControllerClient) {
        this.stockControllerClient = stockControllerClient
        this.waiterControllerClient = waiterControllerClient
        this.markupControllerClient = markupControllerClient
    }

    @Produces(MediaType.TEXT_HTML)
    @Get(uri = '/')
    HttpResponse index() {
        HttpResponse.redirect(URI.create('/index.html'))
    }


    @Get('/stock')
    Single stock() {

        stockControllerClient.list()
                .onErrorReturnItem(Collections.emptyList())
    }

    @Get('/pets/{slug}')
    Maybe<Beer> showPet(@Parameter('slug') String slug) {
        petClient.find slug
    }

    @Get('/pets/random')
    Maybe<Beer> randomPet() {
        petClient.random()
    }


    @Get('/pets/vendor/{vendor}')
    Single<List<Beer>> petsForVendor(String vendor) {
        petClient.byVendor(vendor)
                .onErrorReturnItem(Collections.emptyList())
    }

    @Get('/vendors')
    Single<List<Beer>> vendors() {
        vendorClient.list()
                    .onErrorReturnItem(Collections.emptyList())
    }

}
