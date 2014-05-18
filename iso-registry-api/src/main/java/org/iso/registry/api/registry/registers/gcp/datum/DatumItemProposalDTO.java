package org.iso.registry.api.registry.registers.gcp.datum;

import java.util.Date;

import javax.persistence.EntityManager;

import org.iso.registry.api.IdentifiedItemProposalDTO;
import org.iso.registry.api.registry.registers.gcp.UnitOfMeasureItemProposalDTO;
import org.iso.registry.api.registry.registers.gcp.crs.AreaItemProposalDTO;
import org.iso.registry.core.model.UnitOfMeasureItem;
import org.iso.registry.core.model.crs.AreaItem;
import org.iso.registry.core.model.cs.CoordinateSystemAxisItem;
import org.iso.registry.core.model.datum.DatumItem;
import org.iso.registry.core.model.datum.EllipsoidItem;
import org.iso.registry.core.model.datum.GeodeticDatumItem;
import org.iso.registry.core.model.datum.PrimeMeridianItem;

import de.geoinfoffm.registry.core.model.iso19135.RE_RegisterItem;

public class DatumItemProposalDTO extends IdentifiedItemProposalDTO
{
	public enum DatumType {
		ENGINEERING,
		GEODETIC,
		IMAGE,
		VERTICAL
	}
	
	private DatumType type;
	
	private String anchorDefinition;
	private AreaItemProposalDTO domainOfValidity;
	private Date realizationEpoch;
	private String scope;
	
	private EllipsoidItemProposalDTO ellipsoid;
	private PrimeMeridianItemProposalDTO primeMeridian;
		
	public DatumItemProposalDTO() { }
	
	public DatumItemProposalDTO(DatumItem item) {
		super(item);
	}
	
	public DatumType getType() {
		return this.type;
	}

	public void setType(DatumType type) {
		this.type = type;
	}

	public String getAnchorDefinition() {
		return anchorDefinition;
	}

	public void setAnchorDefinition(String anchorDefinition) {
		this.anchorDefinition = anchorDefinition;
	}

	public AreaItemProposalDTO getDomainOfValidity() {
		return domainOfValidity;
	}

	public void setDomainOfValidity(AreaItemProposalDTO domainOfValidity) {
		this.domainOfValidity = domainOfValidity;
	}

	public Date getRealizationEpoch() {
		return realizationEpoch;
	}

	public void setRealizationEpoch(Date realizationEpoch) {
		this.realizationEpoch = realizationEpoch;
	}

	public String getScope() {
		return scope;
	}

	public void setScope(String scope) {
		this.scope = scope;
	}

	public EllipsoidItemProposalDTO getEllipsoid() {
		return ellipsoid;
	}

	public void setEllipsoid(EllipsoidItemProposalDTO ellipsoid) {
		this.ellipsoid = ellipsoid;
	}

	public PrimeMeridianItemProposalDTO getPrimeMeridian() {
		return primeMeridian;
	}

	public void setPrimeMeridian(PrimeMeridianItemProposalDTO primeMeridian) {
		this.primeMeridian = primeMeridian;
	}

	@Override
	public void setAdditionalValues(RE_RegisterItem item, EntityManager entityManager) {
		super.setAdditionalValues(item, entityManager);
		
		if (item instanceof DatumItem) {
			DatumItem datum = (DatumItem)item;

			datum.setAnchorDefinition(this.getAnchorDefinition());

			if (this.getDomainOfValidity() != null) {
				AreaItem area = entityManager.find(AreaItem.class, this.getDomainOfValidity().getReferencedItemUuid());
				datum.setDomainOfValidity(area);
			}
			
			datum.setRealizationEpoch(this.getRealizationEpoch());
			datum.setScope(this.getScope());
			
			if (item instanceof GeodeticDatumItem) {
				GeodeticDatumItem geodeticDatum = (GeodeticDatumItem)item;
				
				if (this.getEllipsoid() != null) {
					EllipsoidItem ellipsoid = entityManager.find(EllipsoidItem.class, this.getEllipsoid().getReferencedItemUuid());
					geodeticDatum.setEllipsoid(ellipsoid);
				}
				if (this.getPrimeMeridian() != null) {
					PrimeMeridianItem pm = entityManager.find(PrimeMeridianItem.class, this.getPrimeMeridian().getReferencedItemUuid());
					geodeticDatum.setPrimeMeridian(pm);
				}
			}
		}
	}

	@Override
	public void loadAdditionalValues(RE_RegisterItem item) {
		super.loadAdditionalValues(item);
		
		if (item instanceof DatumItem) {
			DatumItem datum = (DatumItem)item;

			this.setAnchorDefinition(datum.getAnchorDefinition());

			if (datum.getDomainOfValidity() != null) {
				this.setDomainOfValidity(new AreaItemProposalDTO(datum.getDomainOfValidity()));
			}
			
			this.setRealizationEpoch(datum.getRealizationEpoch());
			this.setScope(datum.getScope());
			
			if (item instanceof GeodeticDatumItem) {
				GeodeticDatumItem geodeticDatum = (GeodeticDatumItem)item;
				
				if (geodeticDatum.getEllipsoid() != null) {
					this.setEllipsoid(new EllipsoidItemProposalDTO(geodeticDatum.getEllipsoid()));
				}
				if (geodeticDatum.getPrimeMeridian() != null) {
					this.setPrimeMeridian(new PrimeMeridianItemProposalDTO(geodeticDatum.getPrimeMeridian()));
				}
			}
		}
	}
}