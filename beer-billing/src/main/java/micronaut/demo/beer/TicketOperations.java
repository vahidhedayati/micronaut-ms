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
package micronaut.demo.beer;

import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.validation.Validated;
import io.reactivex.Maybe;
import io.reactivex.Single;
import micronaut.demo.beer.domain.CostSync;

import javax.validation.Valid;
import java.util.List;

/**
 * @author graemerocher
 * @since 1.0
 */
@Validated
public interface TicketOperations<T extends CostSync> {

    @Get("/")
    Single<List<T>> list();


    @Get("/username/{name}")
    Single<List<T>> byUsername(String name);

    @Get("/{username}")
    Maybe<T> find(String username);

    @Post("/")
    Single<T> save(@Valid @Body T costSync);
}
