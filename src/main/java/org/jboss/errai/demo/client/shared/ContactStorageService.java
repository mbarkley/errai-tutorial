/**
 * Copyright (C) 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jboss.errai.demo.client.shared;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

/**
 * Defines a JaxRS HTTP service for performing CRUD operations on {@link Contact Contacts}.
 */
@Path("/contact")
public interface ContactStorageService {

  @GET
  @Produces("application/json")
  List<Contact> getAllContacts();

  @POST
  @Consumes("application/json")
  Response create(ContactOperation contactOperation);

  @PUT
  @Consumes("application/json")
  Response update(ContactOperation contactOperation);

  @DELETE
  @Path("/{id:[0-9]+}")
  Response delete(@PathParam("id") Long id);

}
