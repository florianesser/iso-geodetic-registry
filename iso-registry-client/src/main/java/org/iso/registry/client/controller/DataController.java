package org.iso.registry.client.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.hsqldb.lib.StringUtil;
import org.iso.registry.core.model.cs.CoordinateSystemItemRepository;
import org.iso.registry.core.model.datum.DatumItemRepository;
import org.iso.registry.core.model.operation.GeneralOperationParameterItem;
import org.iso.registry.core.model.operation.OperationMethodItem;
import org.iso.registry.core.model.operation.OperationMethodItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/entities")
public class DataController
{
	@Autowired private DatumItemRepository datumRepository;
	@Autowired private CoordinateSystemItemRepository csRepository;
	@Autowired private OperationMethodItemRepository methodRepository;
	
	@Autowired
	private EntityManager entityManager;
	
	@RequestMapping(value = "/by-class/{className}", method = RequestMethod.GET)
	@Transactional(readOnly = true)
	public @ResponseBody List<Object[]> findAll(@PathVariable("className") String className, 
												@RequestParam(value = "orderBy", defaultValue = "code") String orderBy,
												@RequestParam(value = "q", required = false) String search) {
		StringBuilder q = new StringBuilder();
		if (StringUtils.isEmpty(orderBy) || "null".equals(orderBy)) {
			orderBy = "code";
		}
		
		q.append("SELECT i.uuid, i.identifier, i.name, i.description FROM " + className + " i WHERE i.status = 'VALID'");
		if (!StringUtil.isEmpty(search)) {
			search = "%" + search + "%";
			q.append(" AND (LOWER(i.name) LIKE '" + search.toLowerCase() + "' OR CAST(i.identifier AS text) LIKE '" + search + "')");
		}
//		if (filters != null && !filters.isEmpty()) {
//			q.append(" WHERE");
//			boolean first = true;
//			for (String where : filters.get("where")) {
//				String[] parts = where.split(":");
//				if (first) {
//					first = false;
//				}
//				else {
//					q.append(" AND");
//				}
//				q.append(" i.");
//				q.append(parts[0]);
//				q.append(" = ");
//				if (parts.length == 2) {
//					q.append("'");
//					q.append(parts[1]);
//					q.append("'");
//				}
//				else if (parts.length == 3 && parts[1].equalsIgnoreCase("int")) {
//					q.append(parts[1]);
//				}
//			}
//		}
		q.append(" ORDER BY i.");
		q.append(orderBy);
		
		Query query = entityManager.createQuery(q.toString());
//		query.setParameter("className", className);
//		if (!StringUtils.isEmpty(search)) {
//			query.setParameter("search", search);
//		}
		return query.getResultList();
	}
	
	@RequestMapping(value = "/methods/conversion", method = RequestMethod.GET)
	@Transactional(readOnly = true)
	public @ResponseBody List<Object[]> findConversionMethods(@RequestParam(value = "q", required = false) String search) {
		StringBuilder q = new StringBuilder();
		q.append("SELECT i.uuid, i.identifier, i.name FROM ConversionItem i WHERE i.status = 'VALID'");
		if (!StringUtils.isEmpty(search)) {
			search = "%" + search + "%";
			q.append(" AND (LOWER(i.name) LIKE '" + search.toLowerCase() + "' OR CAST(i.identifier AS text) LIKE '" + search + "')");
		}
		q.append(" ORDER BY i.name");
		
		Query query = entityManager.createQuery(q.toString());
		return query.getResultList();
	}
	
	@RequestMapping(value = "/axes", method = RequestMethod.GET)
	@Transactional(readOnly = true)
	public @ResponseBody List<Object[]> findCoordinateAxes(@RequestParam(value = "q", required = false) String search,
														   @RequestParam(value = "uuid", required = false) UUID uuid) {
		StringBuilder q = new StringBuilder();
		if (uuid != null) {
			q.append("SELECT i.uuid, i.identifier, i.name, i.axisAbbreviation, i.axisDirection, i.axisUnit.name FROM CoordinateSystemAxisItem i WHERE i.uuid = '" + uuid.toString() + "'");
			Query query = entityManager.createQuery(q.toString());
			return query.getResultList();
		}
		
		q.append("SELECT i.uuid, i.identifier, i.name, i.axisAbbreviation, i.axisDirection, i.axisUnit.name FROM CoordinateSystemAxisItem i WHERE i.status = 'VALID'");
		if (!StringUtils.isEmpty(search)) {
			search = "%" + search + "%";
			q.append(" AND (");
			
			q.append("LOWER(i.name) LIKE '");
			q.append(search.toLowerCase());
			q.append("'");
			
			q.append(" OR LOWER(i.axisDirection.identifier) LIKE '");
			q.append(search.toLowerCase());
			q.append("'");

			q.append(" OR LOWER(i.axisUnit.name) LIKE '");
			q.append(search.toLowerCase());
			q.append("'");

			q.append("OR CAST(i.identifier AS text) LIKE '");
			q.append(search);
			q.append("'");
			
			q.append(")");
		}
		q.append(" ORDER BY i.name");
		
		Query query = entityManager.createQuery(q.toString());
		return query.getResultList();
	}


	@RequestMapping(value = "/methods/transformation", method = RequestMethod.GET)
	@Transactional(readOnly = true)
	public @ResponseBody List<Object[]> findTransformationMethods(@RequestParam(value = "q", required = false) String search) {
		StringBuilder q = new StringBuilder();
		q.append("SELECT i.uuid, i.identifier, i.name FROM TransformationItem i WHERE i.status = 'VALID'");
		if (!StringUtils.isEmpty(search)) {
			search = "%" + search + "%";
			q.append(" AND (LOWER(i.name) LIKE '" + search.toLowerCase() + "' OR CAST(i.identifier AS text) LIKE '" + search + "')");
		}
		q.append(" ORDER BY i.name");
		
		Query query = entityManager.createQuery(q.toString());
		return query.getResultList();
	}

	@RequestMapping(value = "/parameters/{methodUuid}", method = RequestMethod.GET)
	@Transactional(readOnly = true)
	public @ResponseBody List<Object[]> findParametersForMethod(@PathVariable("methodUuid") UUID methodUuid,
																  @RequestParam(value = "sEcho", defaultValue = "1") String sEcho) {
//		String jpql = "SELECT i.parameter.uuid, i.parameter.code, i.parameter.name FROM OperationMethodItem i WHERE i.uuid ='" + methodUuid.toString() + "'";
		
		List<Object[]> result = new ArrayList<>();
		OperationMethodItem method = methodRepository.findOne(methodUuid);
		if (method != null) {
			for (GeneralOperationParameterItem parameter : method.getParameter()) {
				result.add(new Object[] { parameter.getUuid().toString(), parameter.getIdentifier(), parameter.getName() });
			}
		}
	
		return result;
//		return new DatatablesResult(result.size(), result.size(), sEcho, method.getParameter());
//		return entityManager.createQuery(jpql).getResultList();
	}

	@RequestMapping(value = "/identifier/next-available", method = RequestMethod.GET)
	@Transactional(readOnly = true)
	public @ResponseBody Integer findNextAvailableIdentifier() {
		String jpql = "SELECT MAX(i.identifier) FROM IdentifiedItem i";
		Integer maxCode = (Integer)entityManager.createQuery(jpql).getResultList().get(0);
		return (maxCode == null) ? 1 : maxCode + 1;
	}

	@RequestMapping(value = "/identifier/check-availability", method = RequestMethod.GET)
	@Transactional(readOnly = true)
	public @ResponseBody Long checkIdentifierAvailability(@RequestParam("identifier") Long identifier, @RequestParam(value = "itemUuid", defaultValue = "") String itemUuid) {
		if (identifier == null) {
			return -1L;
		}
		
		String jpql;
		if (StringUtils.isEmpty(itemUuid)) {
			jpql = "SELECT COUNT(i.identifier) FROM IdentifiedItem i WHERE i.identifier = " + identifier.toString();
		}
		else {
			jpql = String.format("SELECT COUNT(i.identifier) FROM IdentifiedItem i WHERE i.uuid <> %s AND i.identifier = %s", itemUuid, identifier.toString());			
		}
		Long count = (Long)entityManager.createQuery(jpql).getResultList().get(0);
		return count;
	}

	@RequestMapping(value = "/by-uuid/{itemUuid}", method = RequestMethod.GET)
	@Transactional(readOnly = true)
	public @ResponseBody List<Object[]> findByUuid(@PathVariable("itemUuid") UUID uuid, @RequestParam(value = "orderBy", defaultValue = "code") String orderBy) {
		String jpql = "SELECT i.uuid, i.identifier, i.name FROM RE_RegisterItem i WHERE uuid = '" + uuid.toString() + "' ORDER BY i." + orderBy;
		return entityManager.createQuery(jpql).getResultList();
	}

}
