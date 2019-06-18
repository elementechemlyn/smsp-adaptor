
package uk.hscic.itk.pds;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for distributionHeaderType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="distributionHeaderType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="addresslist" type="{urn:nhs-itk:ns:201005}addressListType" minOccurs="0"/&gt;
 *         &lt;element name="auditIdentity" type="{urn:nhs-itk:ns:201005}auditIdentityType" minOccurs="0"/&gt;
 *         &lt;element name="manifest" type="{urn:nhs-itk:ns:201005}manifestType"/&gt;
 *         &lt;element name="senderAddress" type="{urn:nhs-itk:ns:201005}addressType" minOccurs="0"/&gt;
 *         &lt;element name="handlingSpecification" type="{urn:nhs-itk:ns:201005}handlingType" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="trackingid" use="required" type="{urn:nhs-itk:ns:201005}uuid" /&gt;
 *       &lt;attribute name="service" use="required" type="{http://www.w3.org/2001/XMLSchema}anyURI" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "distributionHeaderType", propOrder = {
    "addresslist",
    "auditIdentity",
    "manifest",
    "senderAddress",
    "handlingSpecification"
})
public class DistributionHeaderType {

    protected AddressListType addresslist;
    protected AuditIdentityType auditIdentity;
    @XmlElement(required = true)
    protected ManifestType manifest;
    protected AddressType senderAddress;
    protected HandlingType handlingSpecification;
    @XmlAttribute(name = "trackingid", required = true)
    protected String trackingid;
    @XmlAttribute(name = "service", required = true)
    @XmlSchemaType(name = "anyURI")
    protected String service;

    /**
     * Gets the value of the addresslist property.
     * 
     * @return
     *     possible object is
     *     {@link AddressListType }
     *     
     */
    public AddressListType getAddresslist() {
        return addresslist;
    }

    /**
     * Sets the value of the addresslist property.
     * 
     * @param value
     *     allowed object is
     *     {@link AddressListType }
     *     
     */
    public void setAddresslist(AddressListType value) {
        this.addresslist = value;
    }

    /**
     * Gets the value of the auditIdentity property.
     * 
     * @return
     *     possible object is
     *     {@link AuditIdentityType }
     *     
     */
    public AuditIdentityType getAuditIdentity() {
        return auditIdentity;
    }

    /**
     * Sets the value of the auditIdentity property.
     * 
     * @param value
     *     allowed object is
     *     {@link AuditIdentityType }
     *     
     */
    public void setAuditIdentity(AuditIdentityType value) {
        this.auditIdentity = value;
    }

    /**
     * Gets the value of the manifest property.
     * 
     * @return
     *     possible object is
     *     {@link ManifestType }
     *     
     */
    public ManifestType getManifest() {
        return manifest;
    }

    /**
     * Sets the value of the manifest property.
     * 
     * @param value
     *     allowed object is
     *     {@link ManifestType }
     *     
     */
    public void setManifest(ManifestType value) {
        this.manifest = value;
    }

    /**
     * Gets the value of the senderAddress property.
     * 
     * @return
     *     possible object is
     *     {@link AddressType }
     *     
     */
    public AddressType getSenderAddress() {
        return senderAddress;
    }

    /**
     * Sets the value of the senderAddress property.
     * 
     * @param value
     *     allowed object is
     *     {@link AddressType }
     *     
     */
    public void setSenderAddress(AddressType value) {
        this.senderAddress = value;
    }

    /**
     * Gets the value of the handlingSpecification property.
     * 
     * @return
     *     possible object is
     *     {@link HandlingType }
     *     
     */
    public HandlingType getHandlingSpecification() {
        return handlingSpecification;
    }

    /**
     * Sets the value of the handlingSpecification property.
     * 
     * @param value
     *     allowed object is
     *     {@link HandlingType }
     *     
     */
    public void setHandlingSpecification(HandlingType value) {
        this.handlingSpecification = value;
    }

    /**
     * Gets the value of the trackingid property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTrackingid() {
        return trackingid;
    }

    /**
     * Sets the value of the trackingid property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTrackingid(String value) {
        this.trackingid = value;
    }

    /**
     * Gets the value of the service property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getService() {
        return service;
    }

    /**
     * Sets the value of the service property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setService(String value) {
        this.service = value;
    }

}
