using System.Collections;
using System.Data;
using System.Reflection;
using System.Text;
using fit;
using fitnesse.handlers;

using MbUnit.Framework;
namespace dbfit.fixture {
    /* to be re-synchronised with 1.4 release
     
		public class DBFitTestInit {
			public static void InitAssembliesAndNamespaces() {
				TestUtils.InitAssembliesAndNamespaces();
				ObjectFactory.AddAssembly(Assembly.GetAssembly(typeof(DBFitTestInit)).CodeBase);
				ObjectFactory.AddNamespace("dbfit");	
			}
		}
		[TestFixture]
		public class DataTableFixtureTest {
			[Test]
			public void TestExpectBlankOrNullAllCorrect() {
				
				DoTable(
					BuildTable(new string[] { "null", "blank", "joe" }),
					BuildObjectArray(new string[] { null, "", "joe" }),
					3, 0, 0, 0
					);
				DoTable(
					BuildTable(new string[] { "Null", "Blank" }),
					BuildObjectArray(new string[] { null, "" }),
					2, 0, 0, 0
					);
				DoTable(
					BuildTable(new string[] { "NULL", "BLANK" }),
					BuildObjectArray(new string[] { null, "" }),
					2, 0, 0, 0
					);
			}
			[Test]
			public void TestExpectBlankOrNullSomeWrong() {
				
				Parse table = BuildTable(new string[] { "blank", "null" });
				DoTable(
					table,
					BuildObjectArray(new string[] { "", "this is not null" }),
					1, 2, 0, 0
					);
			}

			[Test]
			public void TestOrderedIncorrectly() {
				Parse table = BuildTable(new string[] { "elem1", "elem2","elem3" });
				DoTable(
					table,
					BuildObjectArray(new string[] { "elem3", "elem2", "elem1" }),
					1, 2, 0, 0, true
					);
			}
			[Test]
			public void TestOrderedCorrectly() {
				Parse table = BuildTable(new string[] { "elem1", "elem2", "elem3" });
				DoTable(
					table,
					BuildObjectArray(new string[] { "elem1", "elem2", "elem3" }),
					3, 0, 0, 0, true
					);
			}
			private Parse BuildTable(string[] values) {
				StringBuilder builder = new StringBuilder();
				builder.Append("<table>");
				builder.Append("<tr><td>BusinessObjectDataTableFixture</td></tr>");
				builder.Append("<tr><td>GetFirstString</td></tr>");
				foreach (string value in values) {
					builder.Append("<tr><td>" + value + "</td></tr>");
				}
				builder.Append("</table>");
				return new Parse(builder.ToString());
			}

			private DataTable BuildObjectArray(string[] values) {
				DataTable dt=new DataTable();
				dt.Columns.Add("GetFirstString",typeof(string));
				foreach (string value in values) {
					dt.Rows.Add(new string[] { value });
				}
				return dt;
			}

			public void DoTable(Parse tables, DataTable businessObjects, int right, int wrong, int ignores, int exceptions) {
				DoTable(tables, businessObjects, right, wrong, ignores, exceptions, false);
			}

			public void DoTable(Parse tables, DataTable businessObjects, int right, int wrong, int ignores, int exceptions,bool ordered) {
				fixture=new Fixture();
				BusinessObjectDataTableFixture.table = businessObjects;
				BusinessObjectDataTableFixture.ordered = ordered;
				fixture.DoTables(tables);

				Assert.AreEqual(right, fixture.Counts.Right);
				Assert.AreEqual(wrong, fixture.Counts.Wrong);
				Assert.AreEqual(ignores, fixture.Counts.Ignores);
				Assert.AreEqual(exceptions, fixture.Counts.Exceptions);
			}

			[Test]
			public void TestSurplus() {
				
				
				StringBuilder builder = new StringBuilder();
				builder.Append("<table>");
				builder.Append("<tr><td>BusinessObjectDataTableFixture</td></tr>");
				builder.Append("<tr><td>GetFirstString</td></tr>");
				builder.Append("<tr><td>number1</td></tr>");
				builder.Append("</table>");
				Parse parse = new Parse(builder.ToString());

				BusinessObjectDataTableFixture.table=BuildObjectArray(
					new string[] {"number1","number2","number3"});

				fixture.DoTables(parse);
				Assert.IsTrue(parse.ToString().IndexOf("number1") > 0);
				Assert.IsTrue(parse.ToString().IndexOf("number2") > 0);
				Assert.IsTrue(parse.ToString().IndexOf("number3") > 0);
				AbstractDataTableFixture dataTableFixture = (AbstractDataTableFixture)Fixture.LastFixtureLoaded;
				Assert.AreEqual(1, dataTableFixture.Counts.Right);
				Assert.AreEqual(2, dataTableFixture.Counts.Wrong);
			}

			[Test]
			public void TestMissing() {
				
				
				StringBuilder builder = new StringBuilder();
				builder.Append("<table>");
				builder.Append("<tr><td>BusinessObjectDataTableFixture</td></tr>");
				builder.Append("<tr><td>GetFirstString</td></tr>");
				builder.Append("<tr><td>number1</td></tr>");
				builder.Append("<tr><td>number2</td></tr>");
				builder.Append("<tr><td>number3</td></tr>");
				builder.Append("</table>");
				Parse parse = new Parse(builder.ToString());

				BusinessObjectDataTableFixture.table = BuildObjectArray(
					new string[] { "number1"});

				
				fixture.DoTables(parse);
				Assert.IsTrue(parse.ToString().IndexOf("number1") > 0);
				Assert.IsTrue(parse.ToString().IndexOf("number2") > 0);
				Assert.IsTrue(parse.ToString().IndexOf("number3") > 0);
				AbstractDataTableFixture dataTableFixture = (AbstractDataTableFixture)Fixture.LastFixtureLoaded;
				Assert.AreEqual(2, dataTableFixture.Counts.Wrong);
			}
			private Parse table;
			//private Fixture fixture;
			
			[Test]
			public void TestStartsWithHandlerInSecondColumn() {
				CellOperation.LoadHandler(new StartsWithHandler());
				StringBuilder builder = new StringBuilder();
				builder.Append("<table>");
				builder.Append("<tr><td>people data table fixture</td></tr>");
				builder.Append("<tr><td>first name</td><td>last name</td></tr>");
				builder.Append("<tr><td>Nigel</td><td>Tuf..</td></tr>");
				builder.Append("</table>");
				PeopleLoaderFixture.people.Clear();
				PeopleLoaderFixture.people.Add(new Person("Nigel", "Tufnel"));
				Parse tables = new Parse(builder.ToString());
				Fixture fixture = new Fixture();
				fixture.DoTables(tables);
				Assert.IsTrue(tables.ToString().IndexOf("Tuf..") > -1);
				Assert.IsFalse(tables.ToString().IndexOf("Tufnel") > -1);
				Fixture peopleDataTableFixture = Fixture.LastFixtureLoaded;
				Assert.AreEqual(2, peopleDataTableFixture.Counts.Right);
				Assert.AreEqual(0, peopleDataTableFixture.Counts.Wrong);
				Assert.AreEqual(0, peopleDataTableFixture.Counts.Ignores);
				Assert.AreEqual(0, peopleDataTableFixture.Counts.Exceptions);
			}


			private string dataTableFixtureName = typeof(NewDataTableFixtureDerivative).Name;
			
			Fixture fixture;
			
			[SetUp]
			public void SetUp() {
				DBFitTestInit.InitAssembliesAndNamespaces();
				CellOperation.LoadDefaultHandlers();
				table = new Parse("<table><tr><td>" + dataTableFixtureName + "</td></tr><tr><td>name</td></tr></table>");
				fixture = new Fixture();
				NewDataTableFixtureDerivative.QueryValues.Clear();
			}
			
			[Test]
			public void TestZeroExpectedZeroActual() {
				
				fixture.DoTables(table);
				VerifyCounts(0, 0, 0, 0);
			}
			
			[Test]
			public void TestOneExpectedOneActualCorrect() {
				string name = "Joe";
				AddQueryValue(new DataTableFixturePerson(name));
				AddRow(new string[] { name });
				fixture.DoTables(table);
				VerifyCounts(1, 0, 0, 0);
				AssertTextInTag(table.At(0, 2, 0), "pass");
			}

			[Test]
			public void TestOneExpectedOneActualCorrectTwoColumns() {
				AddColumn(table, "address");
				string name = "Joe";
				string address = "First Street";
				AddQueryValue(new DataTableFixturePerson(name, address));
				AddRow(new string[] { name, address });
				fixture.DoTables(table);
				VerifyCounts(2, 0, 0, 0);
				AssertTextInTag(table.At(0, 2, 0), "pass");
				AssertTextInTag(table.At(0, 2, 1), "pass");
			}

			[Test]
			public void TestTwoColumnAsKeyAllCorrect() {
				AddColumn(table, "address");
				AddColumn(table, "phone?");
				AddRow(new string[] { "Joe", "First Street", "123-1234" });
				AddRow(new string[] { "Joe", "Second Street", "234-2345" });
				AddQueryValue(new DataTableFixturePerson("Joe", "First Street", "123-1234"));
				AddQueryValue(new DataTableFixturePerson("Joe", "Second Street", "234-2345"));
				fixture.DoTables(table);
				VerifyCounts(6, 0, 0, 0);
			}

			[Test]
			public void TestTwoColumnAsKeyThirdColumnIncorrect() {
				AddColumn(table, "address");
				AddColumn(table, "phone?");
				AddRow(new string[] { "Joe", "First Street", "123-1234" });
				AddRow(new string[] { "Joe", "Second Street", "234-2345" });
				AddQueryValue(new DataTableFixturePerson("Joe", "First Street", "123-1234"));
				AddQueryValue(new DataTableFixturePerson("Joe", "Second Street", "234-2346"));
				fixture.DoTables(table);
				VerifyCounts(5, 1, 0, 0);
			}

			[Test]
			public void TestOneExpectedOneActualCorrectTwoColumnsSecondColumnWrong() {
				AddColumn(table, "address?");
				string name = "Joe";
				AddQueryValue(new DataTableFixturePerson(name, "First Street"));
				AddRow(new string[] { name, "Second Street" });
				fixture.DoTables(table);
				VerifyCounts(1, 1, 0, 0);
				AssertTextInTag(table.At(0, 2, 0), "pass");
				AssertTextInTag(table.At(0, 2, 1), "fail");
			}

			[Test]
			public void TestOneExpectedOneActualIncorrect() {
				AddQueryValue(new DataTableFixturePerson("Joe"));
				AddRow(new string[] { "John" });
				fixture.DoTables(table);
				VerifyCounts(0, 2, 0, 0);
				AssertTextInTag(table.At(0, 2, 0), "fail");
				AssertTextInBody(table.At(0, 2, 0), "missing");
				AssertTextInTag(table.At(0, 3, 0), "fail");
				AssertTextInBody(table.At(0, 3, 0), "surplus");
			}

			[Test]
			public void TestDupsAllowed() {
				AddQueryValue(new DataTableFixturePerson("Joe"));
				AddQueryValue(new DataTableFixturePerson("Joe"));
				AddRow(new string[] { "Joe" });
				AddRow(new string[] { "Joe" });
				fixture.DoTables(table);
				VerifyCounts(2, 0, 0, 0);
			}

			[Test]
			public void ThreeItemsWithCommonParts() {
				AddColumn(table, "address");
				AddQueryValue(new DataTableFixturePerson("A", "2"));
				AddQueryValue(new DataTableFixturePerson("B", "1"));
				AddQueryValue(new DataTableFixturePerson("A", "1"));
				AddRow(new string[] { "A", "1" });
				AddRow(new string[] { "A", "2" });
				AddRow(new string[] { "B", "1" });
				fixture.DoTables(table);
				VerifyCounts(6, 0, 0, 0);
			}

			[Test]
			public void TestTwoExpectedTwoActualAllCorrectOrderCorrect() {
				AddQueryValue(new DataTableFixturePerson("Joe"));
				AddQueryValue(new DataTableFixturePerson("Jane"));
				AddRow(new string[] { "Joe" });
				AddRow(new string[] { "Jane" });
				fixture.DoTables(table);
				VerifyCounts(2, 0, 0, 0);
			}

			[Test]
			public void TestTwoExpectedTwoActualAllCorrectOrderIncorrect() {
				AddQueryValue(new DataTableFixturePerson("Joe"));
				AddQueryValue(new DataTableFixturePerson("Jane"));
				AddRow(new string[] { "Jane" });
				AddRow(new string[] { "Joe" });
				fixture.DoTables(table);
				VerifyCounts(2, 0, 0, 0);
			}

			[Test]
			public void TestTwoExpectedTwoActualOneCorrect() {
				AddQueryValue(new DataTableFixturePerson("Joe"));
				AddQueryValue(new DataTableFixturePerson("Jane"));
				AddRow(new string[] { "Joe" });
				AddRow(new string[] { "Susan" });
				fixture.DoTables(table);
				VerifyCounts(1, 2, 0, 0);
			}

			[Test]
			public void TestTwoExpectedTwoActualOneCorrectOrderIncorrect() {
				AddQueryValue(new DataTableFixturePerson("Joe"));
				AddQueryValue(new DataTableFixturePerson("Jane"));
				AddRow(new string[] { "Susan" });
				AddRow(new string[] { "Joe" });
				fixture.DoTables(table);
				VerifyCounts(1, 2, 0, 0);
			}

			[Test]
			public void TestOneMissing() {
				AddRow(new string[] { "Joe" });
				fixture.DoTables(table);
				VerifyCounts(0, 1, 0, 0);
				AssertTextInTag(table.At(0, 2, 0), "fail");
				AssertTextInBody(table.At(0, 2, 0), "missing");
			}

			[Test]
			public void TestOneMissingTwoColumns() {
				AddColumn(table, "address");
				AddRow(new string[] { "Joe", "First Street" });
				fixture.DoTables(table);
				VerifyCounts(0, 1, 0, 0);
				AssertTextInTag(table.At(0, 2, 0), "fail");
				AssertTextInBody(table.At(0, 2, 0), "missing");
			}

			[Test]
			public void TestOnePresentOneMissingTwoColumns() {
				AddColumn(table, "address");
				AddRow(new string[] { "Lilian", "First Street" });
				AddRow(new string[] { "Joe", "Second Street" });
				AddQueryValue(new DataTableFixturePerson("Lilian", "First Street"));
				fixture.DoTables(table);
				VerifyCounts(2, 1, 0, 0);
				AssertTextInTag(table.At(0, 2, 0), "pass");
				AssertTextInTag(table.At(0, 2, 1), "pass");
				AssertTextNotInBody(table.At(0, 2, 0), "missing");
				AssertTextInTag(table.At(0, 3, 0), "fail");
				AssertTextInBody(table.At(0, 3, 0), "missing");
			}

			[Test]
			public void TestOnePresentOneMissingTwoColumnsReverseOrder() {
				AddColumn(table, "address");
				AddRow(new string[] { "Joe", "Second Street" });
				AddRow(new string[] { "Lilian", "First Street" });
				AddQueryValue(new DataTableFixturePerson("Lilian", "First Street"));
				fixture.DoTables(table);
				VerifyCounts(2, 1, 0, 0);
				AssertTextInTag(table.At(0, 2, 0), "fail");
				AssertTextInBody(table.At(0, 2, 0), "missing");
				AssertTextNotInBody(table.At(0, 2, 1), "missing");
				AssertTextInTag(table.At(0, 3, 0), "pass");
				AssertTextInTag(table.At(0, 3, 1), "pass");
				AssertTextNotInBody(table.At(0, 3, 0), "missing");
				AssertTextNotInBody(table.At(0, 3, 1), "missing");
			}

			[Test]
			public void TestCorrectFormatForMissing() {
				PeopleLoaderFixture.people.Clear();
				string loaderFixtureHtml = "<table>" +
					"<tr><td colspan=\"3\">people loader fixture</td></tr>" +
					"<tr><td>id</td><td>first name</td><td>last name</td></tr>" +
					"<tr><td>1</td><td>null</td><td>Jones</td></tr>" +
					"<tr><td>2</td><td>Phil</td><td>blank</td></tr>" +
					"</table>";
				string inspectorFixtureHtml = "<table>" +
					"<tr><td colspan=\"3\">people data table fixture</td></tr>" +
					"<tr><td>id</td><td>first name</td><td>last name</td></tr>" +
					"<tr><td>7</td><td>nullest</td><td>Jonesey</td></tr>" +
					"<tr><td>2</td><td>Phil</td><td>blank</td></tr>" +
					"</table>";
				string processedInspectorFixtureHtml = "<table>" +
					"<tr><td colspan=\"3\">people data table fixture</td></tr>" +
					"<tr><td>id</td><td>first name</td><td>last name</td></tr>" +
					"<tr><td class=\"fail\">7 <span class=\"fit_label\">missing</span></td><td>nullest</td><td>Jonesey</td></tr>" +
					"<tr><td class=\"pass\">2</td><td class=\"pass\">Phil</td><td class=\"pass\">blank</td></tr>" +
					"\n<tr>\n<td class=\"fail\"> <span class=\"fit_grey\">1</span> <span class=\"fit_label\">surplus</span></td>\n<td> <span class=\"fit_grey\">null</span></td>\n<td> <span class=\"fit_grey\">Jones</span></td></tr>" +
					"</table>";
				Parse tables = new Parse(loaderFixtureHtml + inspectorFixtureHtml);
				Fixture fixture = new Fixture();
				fixture.DoTables(tables);
				Assert.AreEqual(loaderFixtureHtml + processedInspectorFixtureHtml, tables.ToString());
			}
			private void AddQueryValue(object obj) {
				NewDataTableFixtureDerivative.QueryValues.Add(obj);
			}
			
			private void VerifyCounts(int right, int wrong, int exceptions, int ignores) {
				Assert.AreEqual(right, fixture.Counts.Right);
				Assert.AreEqual(wrong, fixture.Counts.Wrong);
				Assert.AreEqual(exceptions, fixture.Counts.Exceptions);
				Assert.AreEqual(ignores, fixture.Counts.Ignores);
			}
			private void AssertTextInTag(Parse cell, string text) {
				Assert.IsTrue(cell.Tag.IndexOf(text) > -1);
			}

			private void AssertTextInBody(Parse cell, string text) {
				Assert.IsTrue(cell.Body.IndexOf(text) > -1);
			}

			private void AssertTextNotInBody(Parse cell, string text) {
				Assert.IsFalse(cell.Body.IndexOf(text) > -1);
			}

			private void AddColumn(Parse table, string name) {
				table.Parts.More.Parts.Last.More = new Parse("td", name, null, null);
			}
			private void AddRow(string[] strings) {
				Parse lastCell = new Parse("td", strings[strings.Length - 1], null, null);
				for (int i = strings.Length - 1; i > 0; i--) {
					lastCell = new Parse("td", strings[i - 1], null, lastCell);
				}
				table.Parts.Last.More = new Parse("tr", null, lastCell, null);
			}
			
		}

		public class BusinessObjectDataTableFixture : AbstractDataTableFixture {
			
			public static DataTable table;
			public static bool ordered=false;
			protected override DataTable GetDataTable() {
				return table;
			}
			protected override bool IsOrdered { get { return ordered; }  }
		}
		public class PeopleDataTableFixture : AbstractDataTableFixture {
			
			protected override DataTable GetDataTable() {
				DataTable dt=new DataTable();
				
				dt.Columns.Add("FirstName", typeof(string));
				dt.Columns.Add("LastName", typeof(string));
				dt.Columns.Add("Id", typeof(int));
				dt.Columns.Add("IsTalented", typeof(bool));

				foreach (Person p in PeopleLoaderFixture.people) {
					dt.Rows.Add(new object[] { p.FirstName, p.LastName, p.Id, p.IsTalented });
				}
				return dt;
			}
			protected override bool IsOrdered { get { return false; } }
		}

	
		public class NewDataTableFixtureDerivative : AbstractDataTableFixture {
			
			public static ArrayList QueryValues = new ArrayList();

			protected override DataTable GetDataTable() {
				DataTable dt = new DataTable();

				dt.Columns.Add("Name", typeof(string));
				dt.Columns.Add("Address", typeof(string));
				dt.Columns.Add("Phone", typeof(string));
				

				foreach (DataTableFixturePerson p in QueryValues) {
					dt.Rows.Add(new object[] { p.Name, p.Address, p.Phone });
				}
				return dt;
			}
			protected override bool IsOrdered { get { return false; } }
		}

	public class DataTableFixturePerson {
		public DataTableFixturePerson(string name) {
			this.name = name;
		}

		public DataTableFixturePerson(string name, string address) {
			this.name = name;
			this.address = address;
		}

		public DataTableFixturePerson(string name, string address, string phone) {
			this.name = name;
			this.address = address;
			this.phone = phone;
		}

		public string Name {
			get { return name; }
		}

		public string Address {
			get { return address; }
		}

		public string Phone {
			get { return phone; }
		}

		private string name;
		private string address;
		private string phone;
	}

	
	*/	
}

