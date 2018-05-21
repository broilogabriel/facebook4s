package domain.profile

import domain.FacebookAttribute

object FacebookUserAttribute {
  val defaultAttributeValues = Seq(Email, Name, Id, Picture, FirstName, LastName, Link, AgeRange, Gender,
    Hometown)

  case object Email extends FacebookUserAttribute {
    override def value = "email"
  }

  case object Name extends FacebookUserAttribute {
    override def value = "name"
  }

  case object Id extends FacebookUserAttribute {
    override def value = "id"
  }

  case object Picture extends FacebookUserAttribute {
    override def value = "picture"
  }

  case object FirstName extends FacebookUserAttribute {
    override def value = "first_name"
  }

  case object LastName extends FacebookUserAttribute {
    override def value = "last_name"
  }

  case object Link extends FacebookUserAttribute {
    override def value = "link"
  }

  case object Gender extends FacebookUserAttribute {
    override def value = "gender"
  }

  case object AgeRange extends FacebookUserAttribute {
    override def value = "age_range"
  }

  case object Hometown extends FacebookUserAttribute {
    override def value = "hometown"
  }
}

trait FacebookUserAttribute extends FacebookAttribute {
  def value: String
}
