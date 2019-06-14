
package uk.gov.hscic.schema;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;


/**
 * <p>Java class for ToolkitErrorInfoStruct complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ToolkitErrorInfoStruct"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="ErrorID" type="{http://www.w3.org/2001/XMLSchema}anyURI"/&gt;
 *         &lt;element name="ErrorCode"&gt;
 *           &lt;complexType&gt;
 *             &lt;simpleContent&gt;
 *               &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema&gt;string"&gt;
 *                 &lt;attribute name="codeSystem" type="{http://www.w3.org/2001/XMLSchema}string" default="2.16.840.1.113883.2.1.3.2.4.17.268" /&gt;
 *               &lt;/extension&gt;
 *             &lt;/simpleContent&gt;
 *           &lt;/complexType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="ErrorText" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="ErrorDiagnosticText" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;any namespace='' minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ToolkitErrorInfoStruct", propOrder = {
    "errorID",
    "errorCode",
    "errorText",
    "errorDiagnosticText",
    "any"
})
public class ToolkitErrorInfoStruct {

    @XmlElement(name = "ErrorID", required = true)
    @XmlSchemaType(name = "anyURI")
    protected String errorID;
    @XmlElement(name = "ErrorCode", required = true)
    protected ToolkitErrorInfoStruct.ErrorCode errorCode;
    @XmlElement(name = "ErrorText", required = true)
    protected String errorText;
    @XmlElement(name = "ErrorDiagnosticText")
    protected String errorDiagnosticText;
    @XmlAnyElement(lax = true)
    protected Object any;

    /**
     * Gets the value of the errorID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getErrorID() {
        return errorID;
    }

    /**
     * Sets the value of the errorID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setErrorID(String value) {
        this.errorID = value;
    }

    /**
     * Gets the value of the errorCode property.
     * 
     * @return
     *     possible object is
     *     {@link ToolkitErrorInfoStruct.ErrorCode }
     *     
     */
    public ToolkitErrorInfoStruct.ErrorCode getErrorCode() {
        return errorCode;
    }

    /**
     * Sets the value of the errorCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link ToolkitErrorInfoStruct.ErrorCode }
     *     
     */
    public void setErrorCode(ToolkitErrorInfoStruct.ErrorCode value) {
        this.errorCode = value;
    }

    /**
     * Gets the value of the errorText property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getErrorText() {
        return errorText;
    }

    /**
     * Sets the value of the errorText property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setErrorText(String value) {
        this.errorText = value;
    }

    /**
     * Gets the value of the errorDiagnosticText property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getErrorDiagnosticText() {
        return errorDiagnosticText;
    }

    /**
     * Sets the value of the errorDiagnosticText property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setErrorDiagnosticText(String value) {
        this.errorDiagnosticText = value;
    }

    /**
     * Gets the value of the any property.
     * 
     * @return
     *     possible object is
     *     {@link Object }
     *     
     */
    public Object getAny() {
        return any;
    }

    /**
     * Sets the value of the any property.
     * 
     * @param value
     *     allowed object is
     *     {@link Object }
     *     
     */
    public void setAny(Object value) {
        this.any = value;
    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType&gt;
     *   &lt;simpleContent&gt;
     *     &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema&gt;string"&gt;
     *       &lt;attribute name="codeSystem" type="{http://www.w3.org/2001/XMLSchema}string" default="2.16.840.1.113883.2.1.3.2.4.17.268" /&gt;
     *     &lt;/extension&gt;
     *   &lt;/simpleContent&gt;
     * &lt;/complexType&gt;
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "value"
    })
    public static class ErrorCode {

        @XmlValue
        protected String value;
        @XmlAttribute(name = "codeSystem")
        protected String codeSystem;

        /**
         * Gets the value of the value property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getValue() {
            return value;
        }

        /**
         * Sets the value of the value property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setValue(String value) {
            this.value = value;
        }

        /**
         * Gets the value of the codeSystem property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getCodeSystem() {
            if (codeSystem == null) {
                return "2.16.840.1.113883.2.1.3.2.4.17.268";
            } else {
                return codeSystem;
            }
        }

        /**
         * Sets the value of the codeSystem property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setCodeSystem(String value) {
            this.codeSystem = value;
        }

    }

}
