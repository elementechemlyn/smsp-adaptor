package uk.gov.wildfyre.smsp.dao;

import org.hl7.fhir.dstu3.model.*;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import java.text.SimpleDateFormat;

public class SMSPHL7v3ToFHIRPatientTransform {

    Node node;

    private Patient patient;

    public SMSPHL7v3ToFHIRPatientTransform(Node startNode) {

        node = startNode;
        patient = new Patient();

    }

    public Patient transform() {
        while (node != null) {
            switch (node.getNodeName()) {
                case "id":
                    Node id = node.getAttributes().getNamedItem("extension");
                    if ((id != null)) {
                        patient.setId(id.getNodeValue());
                        Identifier nhsid = patient.addIdentifier();
                        nhsid.setValue(id.getNodeValue()).setSystem("https://fhir.nhs.uk/Id/nhs-number");
                        nhsid.addExtension().setUrl("https://fhir.hl7.org.uk/STU3/StructureDefinition/Extension-CareConnect-NHSNumberVerificationStatus-1")
                                .setValue(new CodeableConcept().addCoding(new Coding().setSystem("https://fhir.hl7.org.uk/STU3/CodeSystem/CareConnect-NHSNumberVerificationStatus-1")
                                        .setCode("01")
                                        .setDisplay("Number present and verified")));
                    }
                    break;
                case "name":
                    processName(node);
                    break;
                case "addr":
                    processAddress(node);
                    break;
                case "telecom":
                    processTelecom(node);
                    break;
                case "patientPerson":
                    processPerson(node);
                    break;
                default:
                    break;
            }
            node = node.getNextSibling();
        }
        return patient;
    }

    private void processPerson(Node node) {
        NodeList pernode = node.getChildNodes();

        for (int g = 0; g < pernode.getLength(); g++) {
            switch (pernode.item(g).getNodeName()) {
                case "administrativeGenderCode":
                    Node adgender = pernode.item(g).getAttributes().getNamedItem("code");
                    switch (adgender.getNodeValue()) {
                        case "1":
                            patient.setGender(Enumerations.AdministrativeGender.MALE);
                            break;
                        case "2":
                            patient.setGender(Enumerations.AdministrativeGender.FEMALE);
                            break;
                        case "9":
                            patient.setGender(Enumerations.AdministrativeGender.OTHER);
                            break;
                        case "X":
                            patient.setGender(Enumerations.AdministrativeGender.UNKNOWN);
                            break;
                        default:
                            break;
                    }
                    break;
                case "birthTime":
                    Node dobnode = pernode.item(g).getAttributes().getNamedItem("value");
                    try {
                        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
                        patient.setBirthDate(format.parse(dobnode.getNodeValue()));
                    } catch (Exception ignore) {
                        // No action
                    }
                    break;
                case "gPPractice":
                    if (pernode.item(g).hasChildNodes()) {
                        NodeList gpnode = pernode.item(g).getChildNodes();
                        processPractice(gpnode);
                    }
                    break;
                default:
                    break;
            }
        }
    }

    private void processPractice(NodeList gpnode) {

            for (int h = 0; h < gpnode.getLength(); h++) {

                if (gpnode.item(h).getNodeName().equals("locationOrganization")) {
                    NodeList gpsubnode = gpnode.item(h).getChildNodes();

                    for (int i = 0; i < gpsubnode.getLength(); i++) {
                        if (gpsubnode.item(i).getNodeName().equals("id")) {
                            Node idnode = gpsubnode.item(i).getAttributes().getNamedItem("extension");
                            patient.getManagingOrganization()
                                    .getIdentifier()
                                    .setSystem("https://fhir.nhs.uk/Id/ods-organization-code")
                                    .setValue(idnode.getNodeValue());
                        }
                        if (gpsubnode.item(i).getNodeName().equals("name")) {

                            patient.getManagingOrganization().setDisplay(gpsubnode.item(i).getTextContent());
                        }
                    }
                }
            }

    }
    private void processName(Node node) {
        NodeList subnode = node.getChildNodes();
        HumanName name = patient.addName();
        for (int g = 0; g < subnode.getLength(); g++) {
            switch (subnode.item(g).getNodeName()) {
                case "prefix":
                    name.addPrefix(subnode.item(g).getTextContent());
                    break;
                case "given":
                    name.addGiven(subnode.item(g).getTextContent());
                    break;
                case "family":
                    name.setFamily(subnode.item(g).getTextContent());
                    break;
                default:
                    break;
            }
        }
    }

    private void processAddress(Node node) {

        NodeList adrnode = node.getChildNodes();
        Address address = patient.addAddress();
        Node type = node.getAttributes().getNamedItem("use");
        switch (type.getNodeValue()) {
            case "HP":
            case "H":
                address.setUse(Address.AddressUse.HOME);
                break;
            case "WP":
                address.setUse(Address.AddressUse.WORK);
                break;
            case "TMP":
                address.setUse(Address.AddressUse.HOME);
                break;
            case "PST" :
                address.setUse(Address.AddressUse.NULL);
                address.setType(Address.AddressType.POSTAL);
                break;
            default:
                break;
        }
        for (int g = 0; g < adrnode.getLength(); g++) {
            switch (adrnode.item(g).getNodeName()) {
                case "postalCode":
                    address.setPostalCode(adrnode.item(g).getTextContent());
                    break;
                case "streetAddressLine":
                    address.addLine(adrnode.item(g).getTextContent());
                    break;
                default:
                    break;
            }
        }

    }
    private void processTelecom(Node node) {
        ContactPoint contact = patient.addTelecom();
        Node use = node.getAttributes().getNamedItem("use");
        if (use != null) {
            switch (node.getNodeValue()) {
                case "H" :
                case "HP" :
                    contact.setUse(ContactPoint.ContactPointUse.HOME);
                    break;
                case "WP" :
                    contact.setUse(ContactPoint.ContactPointUse.WORK);
                    break;
                case "MC" :
                    contact.setUse(ContactPoint.ContactPointUse.MOBILE);
                    break;
                case "PG" :
                    contact.setSystem(ContactPoint.ContactPointSystem.PAGER);
                    break;
                default:
                    break;
            }
        }
        Node value = node.getAttributes().getNamedItem("value");
        if (value != null) {
            contact.setValue(value.getNodeValue());
            if (value.getNodeValue().contains("fax")) {
                contact.setSystem(ContactPoint.ContactPointSystem.FAX);
            }
            if (value.getNodeValue().contains("tel")) {
                contact.setSystem(ContactPoint.ContactPointSystem.PHONE);
            }
            if (value.getNodeValue().contains("@")) {
                contact.setSystem(ContactPoint.ContactPointSystem.EMAIL);
            }
        }

    }
}
