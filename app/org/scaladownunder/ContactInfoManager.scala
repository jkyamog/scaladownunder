package org.scaladownunder

class ContactInfoManager { // bean managed by spring

  var dao: Dao = _ // wired by spring

  def findOrCreate(cType: ContactType, cValue: String, cLabel: String): ContactInfo = {

    val contactInfos = dao.findAll[ContactInfo]

    contactInfos.find(_.contactValue == cValue).getOrElse {
      dao create new ContactInfo {
          contactType = cType
          contactValue = cValue
          contactLabel = cLabel
        }
    }
  }

  def isValidContact(contactType: ContactType, contactValue: String, contactLabel: String): Boolean = {
    import ContactType._

    contactType match {
      case PHONE | MOBILE if isValid[Phone](contactValue) => true
      case WORK_ADDRESS | HOME_ADDRESS if !contactValue.isEmpty => true
      case EMAIL if isValid[Email](contactValue) => true
      case _ => false
    }
  }

  def isValid[T <: Contact](s: String) = true

}

class ContactInfo { // entity managed by jpa
  var contactType: ContactType = _
  var contactValue: String = _
  var contactLabel: String = _
}

trait Contact
class Email extends Contact
class Phone extends Contact