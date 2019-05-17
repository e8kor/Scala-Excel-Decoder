# Scala Excel Decoder [ ![Download](https://api.bintray.com/packages/e8kor/maven/excel/images/download.svg?version=0.0.1) ](https://bintray.com/e8kor/maven/excel/0.0.1/link)
Scala Excel Decoder library, takes excel workbook, its areas, and parses Scala structures from it.

## HOW TO
Library has a goal to provide simplistic user API. Please follow example below.

```scala
import cats.implicits._
import excel.address._
import excel.book._
import excel.decoder.RowDecoder
import excel.decoder.implicits._
import excel.ops._

case class Employee(id: Int, name: String, surname: String, title: String)

object Employee {
  implicit val dec: RowDecoder[Employee] = Employee.apply _
}

for {
 book <- Book("example.xlsx")
 items <- book[Employee](SheetAddress("Employees"))
} yield items

for {
 book <- Book("example.xlsx")
 items <- book[Employee](AreaAddress("Sheet2", 2, 1, 4, 4))
} yield items

```

## License

Scala Excel Decoder is licensed under the Apache License, Version 2.0 (the "License"); you may not use this software except in compliance with the License.

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.