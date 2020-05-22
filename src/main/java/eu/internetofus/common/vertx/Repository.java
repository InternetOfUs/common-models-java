/*
 * -----------------------------------------------------------------------------
 *
 * Copyright (c) 2019 - 2022 UDT-IA, IIIA-CSIC
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 * -----------------------------------------------------------------------------
 */

package eu.internetofus.common.vertx;

import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import eu.internetofus.common.components.ValidationErrorException;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.FindOptions;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.ext.mongo.UpdateOptions;

/**
 * A component that manage the persistence of a component.
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class Repository {

	/**
	 * The pool of database connections.
	 */
	protected MongoClient pool;

	/**
	 * Create a new service.
	 *
	 * @param pool to create the connections.
	 */
	public Repository(final MongoClient pool) {

		this.pool = pool;

	}

	/**
	 * Search for a page.
	 *
	 * @param collectionName of the collections that contains the models.
	 * @param query          to obtain the components of the page.
	 * @param options        to apply to the search.
	 * @param resultKey      to store the found models.
	 * @param map            function to apply to each found object or {@code null}
	 *                       to not modify the components.
	 * @param searchHandler  handler to manage the result action.
	 */
	protected void searchPageObject(final String collectionName, final JsonObject query, final FindOptions options,
			final String resultKey, final Consumer<JsonObject> map,
			final Handler<AsyncResult<JsonObject>> searchHandler) {

		this.pool.count(collectionName, query, count -> {

			if (count.failed()) {

				searchHandler.handle(Future.failedFuture(count.cause()));

			} else {

				final long total = count.result().longValue();
				final int offset = options.getSkip();
				final JsonObject page = new JsonObject().put("offset", offset).put("total", total);
				if (total == 0 || offset >= total) {

					searchHandler.handle(Future.succeededFuture(page));

				} else {

					this.pool.findWithOptions(collectionName, query, options, find -> {

						if (find.failed()) {

							searchHandler.handle(Future.failedFuture(find.cause()));

						} else {

							final List<JsonObject> objects = find.result();
							if (map != null) {

								objects.stream().forEach(map);
							}
							page.put(resultKey, objects);
							searchHandler.handle(Future.succeededFuture(page));
						}

					});

				}

			}
		});
	}

	/**
	 * Delete one document.
	 *
	 * @param collectionName of the collections that contains the model to delete.
	 * @param query          to to match the document to delete.
	 * @param deleteHandler  handler to manage the delete action.
	 */
	protected void deleteOneDocument(final String collectionName, final JsonObject query,
			final Handler<AsyncResult<Void>> deleteHandler) {

		this.pool.removeDocument(collectionName, query, remove -> {

			if (remove.failed()) {

				deleteHandler.handle(Future.failedFuture(remove.cause()));

			} else if (remove.result().getRemovedCount() != 1) {

				deleteHandler.handle(Future.failedFuture("Not found document to delete"));

			} else {

				deleteHandler.handle(Future.succeededFuture());
			}
		});

	}

	/**
	 * Update one document.
	 *
	 * @param collectionName of the collections that contains the model to update.
	 * @param query          to to match the document to update.
	 * @param updateModel    the new values of the model.
	 * @param updateHandler  handler to manage the update action.
	 */
	protected void updateOneDocument(final String collectionName, final JsonObject query, final JsonObject updateModel,
			final Handler<AsyncResult<Void>> updateHandler) {

		final JsonObject updateQuery = new JsonObject().put("$set", updateModel);
		final UpdateOptions options = new UpdateOptions().setMulti(false);
		this.pool.updateCollectionWithOptions(collectionName, query, updateQuery, options, update -> {

			if (update.failed()) {

				updateHandler.handle(Future.failedFuture(update.cause()));

			} else if (update.result().getDocModified() != 1) {

				updateHandler.handle(Future.failedFuture("Not found document to update"));

			} else {

				updateHandler.handle(Future.succeededFuture());
			}
		});

	}

	/**
	 * Store one document.
	 *
	 * @param collectionName of the collections that contains the model to store.
	 * @param model          to store.
	 * @param map            function to modify the stored document. If it is
	 *                       {@code null} no modification is applied.
	 *
	 * @param storeHandler   handler to manage the store action.
	 */
	protected void storeOneDocument(final String collectionName, final JsonObject model,
			final Function<JsonObject, JsonObject> map, final Handler<AsyncResult<JsonObject>> storeHandler) {

		this.pool.insert(collectionName, model, store -> {

			if (store.failed()) {

				storeHandler.handle(Future.failedFuture(store.cause()));

			} else {

				if (map != null) {

					try {

						final JsonObject adaptedModel = map.apply(model);
						storeHandler.handle(Future.succeededFuture(adaptedModel));

					} catch (final Throwable throwable) {

						storeHandler.handle(Future.failedFuture(throwable));

					}

				} else {

					storeHandler.handle(Future.succeededFuture(model));
				}
			}

		});
	}

	/**
	 * Find one document.
	 *
	 * @param collectionName of the collections that contains the model to find.
	 * @param query          of the document to find.
	 * @param fields         to return.
	 * @param map            function to modify the found document. If it is
	 *                       {@code null} no modification is applied.
	 * @param searchHandler  handler to manage the find action.
	 */
	protected void findOneDocument(final String collectionName, final JsonObject query, final JsonObject fields,
			final Function<JsonObject, JsonObject> map, final Handler<AsyncResult<JsonObject>> searchHandler) {

		this.pool.findOne(collectionName, query, fields, search -> {

			if (search.failed()) {

				searchHandler.handle(Future.failedFuture(search.cause()));

			} else {

				JsonObject value = search.result();
				if (value == null) {

					searchHandler.handle(Future.failedFuture("Does not exist a document that match '" + query + "'."));

				} else {

					if (map != null) {

						value = map.apply(value);
					}
					searchHandler.handle(Future.succeededFuture(value));
				}
			}
		});

	}

	/**
	 * Convert a set of values to an object that represents the order to the models
	 * to return.
	 *
	 * @param values     to extract how the query has to be sorted.
	 * @param codePrefix prefix to append to the error code.
	 *
	 * @return the object that can be used to sort a query.
	 *
	 * @throws ValidationErrorException If the values are not right.
	 */
	public static JsonObject toSort(final Iterable<String> values, final String codePrefix)
			throws ValidationErrorException {

		if (values == null) {

			return null;

		} else {

			final Iterator<String> iter = values.iterator();
			if (!iter.hasNext()) {

				return null;

			} else {

				JsonObject sort = new JsonObject();
				for (int i = 0; iter.hasNext(); i++) {

					String value = iter.next();
					if (value == null) {

						throw new ValidationErrorException(codePrefix + "[" + i + "]",
								"An order item can not be 'null'.");

					} else {

						value = value.trim();
						final int index = value.indexOf(':');
						if (index < 0) {

							throw new ValidationErrorException(codePrefix + "[" + i + "]",
									"Can not found the field separator in '" + value + "'.");

						} else {

							final String key = value.substring(0, index).trim();
							if (key.isEmpty()) {

								throw new ValidationErrorException(codePrefix + "[" + i + "]",
										"You must to define a field in '" + value + "'.");

							} else if (sort.containsKey(key)) {

								throw new ValidationErrorException(codePrefix + "[" + i + "]",
										"The field '" + key + "' is already defined.");

							} else {

								try {

									final int type = Integer.parseInt(value.substring(index + 1).trim());
									if (type != -1 && type != 1) {

										throw new ValidationErrorException(codePrefix + "[" + i + "]",
												"The '" + type + "' has to be 1 or -1.");

									} else {

										sort = sort.put(key, type);
									}

								} catch (final NumberFormatException error) {

									throw new ValidationErrorException(codePrefix + "[" + i + "]", error);

								}
							}
						}
					}
				}

				return sort;
			}
		}
	}
}
