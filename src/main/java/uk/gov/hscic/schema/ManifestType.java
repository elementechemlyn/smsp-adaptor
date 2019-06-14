
package uk.gov.hscic.schema;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for manifestType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="manifestType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="manifestitem" type="{urn:nhs-itk:ns:201005}manifestItemType" maxOccurs="unbounded"/&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="count" use="required" type="{http://www.w3.org/2001/XMLSchema}integer" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "manifestType", propOrder = {
    "manifestitem"
})
public class ManifestType {

    @XmlElement(required = true)
    protected List<ManifestItemType> manifestitem;
    @XmlAttribute(name = "count", required = true)
    protected BigInteger count;

    /**
     * Gets the value of the manifestitem property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the manifestitem property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getManifestitem().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ManifestItemType }
     * 
     * 
     */
    public List<ManifestItemType> getManifestitem() {
        if (manifestitem == null) {
            manifestitem = new ArrayList<ManifestItemType>();
        }
        return this.manifestitem;
    }

    /**
     * Gets the value of the count property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getCount() {
        return count;
    }

    /**
     * Sets the value of the count property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setCount(BigInteger value) {
        this.count = value;
    }

}
