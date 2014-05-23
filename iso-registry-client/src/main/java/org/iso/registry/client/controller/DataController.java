package org.iso.registry.client.controller;

import java.util.List;
import java.util.UUID;

import javax.persistence.EntityManager;

import org.iso.registry.core.model.crs.AreaItemRepository;
import org.iso.registry.core.model.cs.CoordinateSystemItemRepository;
import org.iso.registry.core.model.datum.DatumItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/entities")
public class DataController
{
	@Autowired private AreaItemRepository areaRepository;
	@Autowired private DatumItemRepository datumRepository;
	@Autowired private CoordinateSystemItemRepository csRepository;
	
	@Autowired
	private EntityManager entityManager;
	
	@RequestMapping(value = "/byClass/{className}", method = RequestMethod.GET)
	@Transactional(readOnly = true)
	public @ResponseBody List<Object[]> findAll(@PathVariable("className") String className, @RequestParam(value = "orderBy", defaultValue = "code") String orderBy) {
		String jpql = "SELECT i.uuid, i.code, i.name FROM " + className + " i ORDER BY i." + orderBy;
		return entityManager.createQuery(jpql).getResultList();
	}

	@RequestMapping(value = "/byUuid/{itemUuid}", method = RequestMethod.GET)
	@Transactional(readOnly = true)
	public @ResponseBody List<Object[]> findByUuid(@PathVariable("itemUuid") UUID uuid, @RequestParam(value = "orderBy", defaultValue = "code") String orderBy) {
		String jpql = "SELECT i.uuid, i.code, i.name FROM RE_RegisterItem i WHERE uuid = '" + uuid.toString() + "' ORDER BY i." + orderBy;
		return entityManager.createQuery(jpql).getResultList();
	}


//	@RequestMapping(value = "/area", method = RequestMethod.GET/*, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE*/)
//	@Transactional(readOnly = true)
//	public @ResponseBody List<Object[]> getAreas(@RequestParam(value = "orderBy", defaultValue = "code") String orderBy) {
//		if (orderBy.equalsIgnoreCase("name")) {
//			return areaRepository.findAllOrderByName();
//		}
//		else {
//			return areaRepository.findAllOrderByCode();
//		}
//	}
//
//	@RequestMapping(value = "/datum", method = RequestMethod.GET/*, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE*/)
//	@Transactional(readOnly = true)
//	public @ResponseBody List<Object[]> getDatums() {
//		return datumRepository.findAllOrderByCode();
//	}
//
//	@RequestMapping(value = "/cs", method = RequestMethod.GET/*, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE*/)
//	@Transactional(readOnly = true)
//	public @ResponseBody List<Object[]> getCoordinateSystems() {
//		return csRepository.findAllOrderByCode();
//	}
//
}
