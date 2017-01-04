package org.egov.mrs.web.controller.application.registration;

import static org.egov.mrs.application.MarriageConstants.CF_STAMP;
import static org.egov.mrs.application.MarriageConstants.MOM;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.egov.mrs.domain.entity.MarriageDocument;
import org.egov.mrs.domain.entity.MarriageRegistration;
import org.egov.mrs.domain.entity.ReIssue;
import org.egov.mrs.domain.service.MarriageDocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

/**
 * @author vinoth
 *
 */
@Component
public class MarriageFormValidator implements Validator {

    private static final String NOTEMPTY_MRG_WITNESS_AGE = "Notempty.mrg.witness.age";
    private static final String NOTEMPTY_MRG_WITNESS_PARENT = "Notempty.mrg.witness.parent";
    private static final String NOTEMPTY_MRG_RESIDENCE_ADDRESS = "Notempty.mrg.residence.address";
    private static final String NOTEMPTY_MRG_OFFICE_ADDRESS = "Notempty.mrg.office.address";
    private static final String NOTEMPTY_MRG_FIRST_NAME = "Notempty.mrg.first.name";
    private static final String NOTEMPTY_MRG_CITY = "Notempty.mrg.city";
    private static final String NOTEMPTY_MRG_LOCALITY = "Notempty.mrg.locality";
    private static final String NOTEMPTY_MRG_STREET = "Notempty.mrg.street";
    private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
            + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    private static final String MOBILE_PATTERN = "[0-9]{10}";
    private Pattern pattern;
    private Matcher matcher;

    @Autowired
    private MarriageDocumentService marriageDocumentService;

    @Override
    public boolean supports(final Class<?> clazz) {
        return MarriageRegistration.class.equals(clazz);
    }
    
    public void validateReIssue(Object target, Errors errors) {
        ReIssue reIssue = (ReIssue) target;
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "marriageRegistrationUnit", "Notempty.regUnit.name");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "applicant.name.firstName", NOTEMPTY_MRG_FIRST_NAME);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "applicant.contactInfo.residenceAddress", NOTEMPTY_MRG_RESIDENCE_ADDRESS);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "applicant.contactInfo.officeAddress", NOTEMPTY_MRG_OFFICE_ADDRESS);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "applicant.contactInfo.mobileNo", "Notempty.mrg.mobile.no");
        
        if(reIssue.getFeePaid()==null)
            errors.reject("Notempty.reissue.fee", null);
       // ValidationUtils.rejectIfEmptyOrWhitespace(errors, "feePaid", "Notempty.reissue.fee");
        if (reIssue.getApplicant() != null && reIssue.getApplicant().getContactInfo() != null
                && reIssue.getApplicant().getContactInfo().getMobileNo() != null) {
            pattern = Pattern.compile(MOBILE_PATTERN);
            matcher = pattern.matcher(reIssue.getApplicant().getContactInfo().getMobileNo());
            if (!matcher.matches()) {
                errors.rejectValue("applicant.contactInfo.mobileNo", "validate.mobile.no");
            }
        }
        
        if (reIssue.getApplicant() != null && reIssue.getApplicant().getContactInfo() != null
                && reIssue.getApplicant().getContactInfo().getEmail() != null) {
            pattern = Pattern.compile(EMAIL_PATTERN);
            matcher = pattern.matcher(reIssue.getApplicant().getContactInfo().getEmail());
            if (!matcher.matches()) {
                errors.rejectValue("applicant.contactInfo.email", "validate.email");
            }
        }
    }
    

    public void validate(final Object target, final Errors errors, final String type) {
        final MarriageRegistration registration = (MarriageRegistration) target;
        if (type != null && "DATAENTRY".equals(type)) {
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "applicationNo", "Notempty.mrg.appln.no");
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "registrationNo", "Notempty.mrg.reg.no");
            validateSerialAndPageNo(errors);
        }
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "marriageRegistrationUnit", "Notempty.regUnit.name");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "street", NOTEMPTY_MRG_STREET);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "locality", NOTEMPTY_MRG_LOCALITY);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "city", NOTEMPTY_MRG_CITY);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "dateOfMarriage", "Notempty.mrg.date.of.mrg");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "venue", "Notempty.mrg.venue");
        if(!"Residence".equals(registration.getVenue())){
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "placeOfMarriage", "Notempty.mrg.place.of.mrg");
        }
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "husband.name.firstName", NOTEMPTY_MRG_FIRST_NAME);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "husband.religion", "Notempty.mrg.religion.name");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "husband.ageInYearsAsOnMarriage", "Notempty.mrg.age.year");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "husband.ageInMonthsAsOnMarriage", "Notempty.mrg.age.month");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "husband.maritalStatus", "Notempty.mrg.marital.status");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "husband.occupation", "Notempty.mrg.occupation");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "husband.parentsName", "Notempty.mrg.parents.name");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "husband.qualification", "Notempty.mrg.education.qualification");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "husband.nationality", "Notempty.mrg.nationality");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "husband.street", NOTEMPTY_MRG_STREET);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "husband.locality", NOTEMPTY_MRG_LOCALITY);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "husband.city", NOTEMPTY_MRG_CITY);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "husband.contactInfo.residenceAddress", NOTEMPTY_MRG_RESIDENCE_ADDRESS);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "husband.contactInfo.officeAddress", NOTEMPTY_MRG_RESIDENCE_ADDRESS);

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "wife.name.firstName", NOTEMPTY_MRG_FIRST_NAME);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "wife.religion", "Notempty.mrg.religion.name");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "wife.ageInYearsAsOnMarriage", "Notempty.mrg.age.year");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "wife.ageInMonthsAsOnMarriage", "Notempty.mrg.age.month");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "wife.maritalStatus", "Notempty.mrg.marital.status");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "wife.occupation", "Notempty.mrg.occupation");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "wife.parentsName", "Notempty.mrg.parents.name");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "wife.qualification", "Notempty.mrg.education.qualification");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "wife.nationality", "Notempty.mrg.nationality");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "wife.street", NOTEMPTY_MRG_STREET);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "wife.locality", NOTEMPTY_MRG_LOCALITY);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "wife.city", NOTEMPTY_MRG_CITY);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "wife.contactInfo.residenceAddress", NOTEMPTY_MRG_RESIDENCE_ADDRESS);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "wife.contactInfo.officeAddress", NOTEMPTY_MRG_RESIDENCE_ADDRESS);

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "witnesses[0].name.firstName", NOTEMPTY_MRG_FIRST_NAME);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "witnesses[0].witnessRelation", NOTEMPTY_MRG_FIRST_NAME);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "witnesses[0].relativeName", NOTEMPTY_MRG_WITNESS_PARENT);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "witnesses[0].age", NOTEMPTY_MRG_WITNESS_AGE);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "witnesses[0].contactInfo.residenceAddress",
                NOTEMPTY_MRG_RESIDENCE_ADDRESS);

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "witnesses[1].name.firstName", NOTEMPTY_MRG_FIRST_NAME);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "witnesses[1].witnessRelation", NOTEMPTY_MRG_FIRST_NAME);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "witnesses[1].relativeName", NOTEMPTY_MRG_WITNESS_PARENT);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "witnesses[1].age", NOTEMPTY_MRG_WITNESS_AGE);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "witnesses[1].contactInfo.residenceAddress",
                NOTEMPTY_MRG_RESIDENCE_ADDRESS);

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "witnesses[2].name.firstName", NOTEMPTY_MRG_FIRST_NAME);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "witnesses[2].witnessRelation", NOTEMPTY_MRG_FIRST_NAME);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "witnesses[2].relativeName", NOTEMPTY_MRG_WITNESS_PARENT);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "witnesses[2].age", NOTEMPTY_MRG_WITNESS_AGE);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "witnesses[2].contactInfo.residenceAddress",
                NOTEMPTY_MRG_RESIDENCE_ADDRESS);

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "witnesses[3].name.firstName", NOTEMPTY_MRG_FIRST_NAME);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "witnesses[3].witnessRelation", NOTEMPTY_MRG_FIRST_NAME);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "witnesses[3].relativeName", NOTEMPTY_MRG_WITNESS_PARENT);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "witnesses[3].age", NOTEMPTY_MRG_WITNESS_AGE);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "witnesses[3].contactInfo.residenceAddress",
                NOTEMPTY_MRG_RESIDENCE_ADDRESS);

        if (registration != null) {
            validateBrideInformation(errors, registration);
            validateBrideGroomInformation(errors, registration);
            validateDocumentAttachments(errors, registration);

            if (registration.getStatus() != null && "CREATED".equals(registration.getStatus().getCode())
                    && !registration.isFeeCollected())
                errors.reject("validate.collect.marriageFee", null);

            if (registration.getStatus() != null && "APPROVED".equals(registration.getStatus().getCode()))
                validateSerialAndPageNo(errors);

        }

    }

    private void validateDocumentAttachments(final Errors errors, final MarriageRegistration registration) {
        if (registration.getId() == null)
            for (final MarriageDocument marriageDocument : registration.getDocuments()) {
                final MarriageDocument Document = marriageDocumentService.get(marriageDocument.getId());
                if (Document.getCode().equals(MOM) && marriageDocument.getFile().getSize() == 0)
                    errors.rejectValue("memorandumOfMarriage", "validate.memorendum");
                if (Document.getCode().equals(CF_STAMP) && marriageDocument.getFile().getSize() == 0)
                    errors.rejectValue("courtFeeStamp", "validate.courtfeettamp");
            }
    }

    private void validateSerialAndPageNo(final Errors errors) {
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "serialNo", "Notempty.mrg.serial.no");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "pageNo", "Notempty.mrg.page.no");
    }

    private void validateBrideGroomInformation(final Errors errors, final MarriageRegistration registration) {
        if (registration.getHusband() != null && registration.getWife().getAgeInYearsAsOnMarriage() != null
                && registration.getWife().getAgeInYearsAsOnMarriage() < 18)
            errors.rejectValue("wife.ageInYearsAsOnMarriage", "Validdata.mrg.ageinyears");
        if (registration.getHusband() != null && registration.getWife().getAgeInMonthsAsOnMarriage() != null
                && registration.getWife().getAgeInMonthsAsOnMarriage() > 12)
            errors.rejectValue("wife.ageInMonthsAsOnMarriage", "Validdata.mrg.ageinmonths");
        validateBrideGroomMobileNo(errors, registration);
        validateBrideGroomEmail(errors, registration);
    }

    private void validateBrideGroomEmail(final Errors errors, final MarriageRegistration registration) {
        if (registration.getWife() != null && registration.getWife().getContactInfo() != null
                && registration.getWife().getContactInfo().getEmail() != null) {
            pattern = Pattern.compile(EMAIL_PATTERN);
            matcher = pattern.matcher(registration.getWife().getContactInfo().getEmail());
            if (!matcher.matches())
                errors.rejectValue("wife.contactInfo.email", "validate.email");
        }
    }

    private void validateBrideGroomMobileNo(final Errors errors, final MarriageRegistration registration) {
        if (registration.getWife() != null && registration.getWife().getContactInfo() != null
                && registration.getWife().getContactInfo().getMobileNo() != null) {
            pattern = Pattern.compile(MOBILE_PATTERN);
            matcher = pattern.matcher(registration.getWife().getContactInfo().getMobileNo());
            if (!matcher.matches())
                errors.rejectValue("wife.contactInfo.mobileNo", "validate.mobile.no");
        }
    }

    private void validateBrideInformation(final Errors errors, final MarriageRegistration registration) {
        if (registration.getHusband() != null && registration.getHusband().getAgeInYearsAsOnMarriage() != null
                && registration.getHusband().getAgeInYearsAsOnMarriage() < 18)
            errors.rejectValue("husband.ageInYearsAsOnMarriage", "Validdata.mrg.ageinyears");
        if (registration.getHusband() != null && registration.getHusband().getAgeInMonthsAsOnMarriage() != null
                && registration.getHusband().getAgeInMonthsAsOnMarriage() > 12)
            errors.rejectValue("husband.ageInMonthsAsOnMarriage", "Validdata.mrg.ageinmonths");
        validateBrideMobileNo(errors, registration);
        validateBrideEmail(errors, registration);
    }

    private void validateBrideEmail(final Errors errors, final MarriageRegistration registration) {
        if (registration.getHusband() != null && registration.getHusband().getContactInfo() != null
                && registration.getHusband().getContactInfo().getEmail() != null) {
            pattern = Pattern.compile(EMAIL_PATTERN);
            matcher = pattern.matcher(registration.getHusband().getContactInfo().getEmail());
            if (!matcher.matches())
                errors.rejectValue("husband.contactInfo.email", "validate.email");
        }
    }

    private void validateBrideMobileNo(final Errors errors, final MarriageRegistration registration) {
        if (registration.getHusband() != null && registration.getHusband().getContactInfo() != null
                && registration.getHusband().getContactInfo().getMobileNo() != null) {
            pattern = Pattern.compile(MOBILE_PATTERN);
            matcher = pattern.matcher(registration.getHusband().getContactInfo().getMobileNo());
            if (!matcher.matches())
                errors.rejectValue("husband.contactInfo.mobileNo", "validate.mobile.no");
        }
    }

    @Override
    public void validate(final Object target, final Errors errors) {
        // we passing additional parameter application type(Registration and Data entry),
        // this same validator class which can be used both for application type.Thats why we are not using this method.
    }

}