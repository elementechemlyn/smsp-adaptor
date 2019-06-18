
package uk.hscic.itk.pds;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the uk.hscic.itk.pds package.
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _ToolkitErrorInfo_QNAME = new QName("urn:nhs-itk:ns:201005", "ToolkitErrorInfo");
    private final static QName _HL7Content_QNAME = new QName("urn:nhs-itk:ns:201005", "HL7Content");
    private final static QName _VersionID_QNAME = new QName("urn:nhs-itk:ns:201005", "VersionID");
    private final static QName _SimpleMessageResponse_QNAME = new QName("urn:nhs-itk:ns:201005", "SimpleMessageResponse");
    private final static QName _DistributionEnvelope_QNAME = new QName("urn:nhs-itk:ns:201005", "DistributionEnvelope");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: uk.hscic.itk.pds
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link ToolkitErrorInfoStruct }
     * 
     */
    public ToolkitErrorInfoStruct createToolkitErrorInfoStruct() {
        return new ToolkitErrorInfoStruct();
    }

    /**
     * Create an instance of {@link DistributionEnvelopeType }
     * 
     */
    public DistributionEnvelopeType createDistributionEnvelopeType() {
        return new DistributionEnvelopeType();
    }

    /**
     * Create an instance of {@link DistributionHeaderType }
     * 
     */
    public DistributionHeaderType createDistributionHeaderType() {
        return new DistributionHeaderType();
    }

    /**
     * Create an instance of {@link HandlingType }
     * 
     */
    public HandlingType createHandlingType() {
        return new HandlingType();
    }

    /**
     * Create an instance of {@link HandlingSpecType }
     * 
     */
    public HandlingSpecType createHandlingSpecType() {
        return new HandlingSpecType();
    }

    /**
     * Create an instance of {@link AddressListType }
     * 
     */
    public AddressListType createAddressListType() {
        return new AddressListType();
    }

    /**
     * Create an instance of {@link AddressType }
     * 
     */
    public AddressType createAddressType() {
        return new AddressType();
    }

    /**
     * Create an instance of {@link IdentityType }
     * 
     */
    public IdentityType createIdentityType() {
        return new IdentityType();
    }

    /**
     * Create an instance of {@link ManifestType }
     * 
     */
    public ManifestType createManifestType() {
        return new ManifestType();
    }

    /**
     * Create an instance of {@link AuditIdentityType }
     * 
     */
    public AuditIdentityType createAuditIdentityType() {
        return new AuditIdentityType();
    }

    /**
     * Create an instance of {@link ManifestItemType }
     * 
     */
    public ManifestItemType createManifestItemType() {
        return new ManifestItemType();
    }

    /**
     * Create an instance of {@link PayloadsType }
     * 
     */
    public PayloadsType createPayloadsType() {
        return new PayloadsType();
    }

    /**
     * Create an instance of {@link PayloadType }
     * 
     */
    public PayloadType createPayloadType() {
        return new PayloadType();
    }

    /**
     * Create an instance of {@link ToolkitErrorInfoStruct.ErrorCode }
     * 
     */
    public ToolkitErrorInfoStruct.ErrorCode createToolkitErrorInfoStructErrorCode() {
        return new ToolkitErrorInfoStruct.ErrorCode();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ToolkitErrorInfoStruct }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link ToolkitErrorInfoStruct }{@code >}
     */
    @XmlElementDecl(namespace = "urn:nhs-itk:ns:201005", name = "ToolkitErrorInfo")
    public JAXBElement<ToolkitErrorInfoStruct> createToolkitErrorInfo(ToolkitErrorInfoStruct value) {
        return new JAXBElement<ToolkitErrorInfoStruct>(_ToolkitErrorInfo_QNAME, ToolkitErrorInfoStruct.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "urn:nhs-itk:ns:201005", name = "HL7Content")
    public JAXBElement<String> createHL7Content(String value) {
        return new JAXBElement<String>(_HL7Content_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "urn:nhs-itk:ns:201005", name = "VersionID")
    public JAXBElement<String> createVersionID(String value) {
        return new JAXBElement<String>(_VersionID_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "urn:nhs-itk:ns:201005", name = "SimpleMessageResponse")
    public JAXBElement<String> createSimpleMessageResponse(String value) {
        return new JAXBElement<String>(_SimpleMessageResponse_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DistributionEnvelopeType }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link DistributionEnvelopeType }{@code >}
     */
    @XmlElementDecl(namespace = "urn:nhs-itk:ns:201005", name = "DistributionEnvelope")
    public JAXBElement<DistributionEnvelopeType> createDistributionEnvelope(DistributionEnvelopeType value) {
        return new JAXBElement<DistributionEnvelopeType>(_DistributionEnvelope_QNAME, DistributionEnvelopeType.class, null, value);
    }

}
