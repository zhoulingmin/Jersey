package sample.hello.resources;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.xml.bind.JAXBElement;

import com.sun.jersey.api.NotFoundException;

public class ContactResource {
	@Context
	UriInfo uriInfo;
	@Context
	Request request;
	String contact;
	
	public ContactResource(UriInfo uriInfo, Request request, 
			String contact) {
		this.uriInfo = uriInfo;
		this.request = request;
		this.contact = contact;
	}
	
	@GET
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	public Contact getContact() {
		Contact cont = ContactStore.getStore().get(contact);
		if(cont==null)
			throw new NotFoundException("No such Contact.");
		return cont;
	}
	
	@POST
	@Produces(MediaType.TEXT_HTML)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public void newContact(
			@FormParam("id") String id,
			@FormParam("name") String name,
			@Context HttpServletResponse servletResponse
	) throws IOException {
		Contact c = new Contact(id,name,new ArrayList<Address>());
		ContactStore.getStore().put(id, c);
			
		URI uri = uriInfo.getAbsolutePathBuilder().path(id).build();
		Response.created(uri).build();
			
		servletResponse.sendRedirect("../pages/new_contact.html");
	}
	
	@PUT
	@Consumes(MediaType.APPLICATION_XML)
	public Response putContact(JAXBElement<Contact> jaxbContact) {
		Contact c = jaxbContact.getValue();
		return putAndGetResponse(c);
	}

	private Response putAndGetResponse(Contact c) {
		Response res;
		if(ContactStore.getStore().containsKey(c.getId())) {
			res = Response.noContent().build();
		} else {
			res = Response.created(uriInfo.getAbsolutePath()).build();
		}
		ContactStore.getStore().put(c.getId(), c);
		return res;
	}
	
	@DELETE
	public void deleteContact() {
		Contact c = ContactStore.getStore().remove(contact);
		if(c==null)
			throw new NotFoundException("No such Contact.");
	}
}
