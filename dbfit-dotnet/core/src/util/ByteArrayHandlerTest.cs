using System;
using System.Collections.Generic;
using System.Text;

using MbUnit.Framework;

namespace dbfit.util {
	[TestFixture]
	public class ByteArrayHandlerTest {

		[Test]
		public void CheckNormaliseName() {
			Assert.AreEqual(new byte[]{16,32,49}, ByteArrayHandler.ParseArray("0x102031"));
			Assert.AreEqual(new byte[] { 16, 32, 49 }, ByteArrayHandler.ParseArray("0x 10 20 31"));
			Assert.AreEqual(new byte[] { 16, 32, 49 }, ByteArrayHandler.ParseArray("0X 10 20 31"));
		}
	}
}
