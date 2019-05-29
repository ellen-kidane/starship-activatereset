package com.rallyhealth.starship-activatereset.controller

import com.rallyhealth.starship-activatereset.models.{HealthCheckResponse, HealthCheckStatus}
import com.rallyhealth.starship-activatereset.service.HealthCheckService
import com.rallyhealth.testkit.play25.OneMaterializerPerSuite
import org.joda.time.Duration
import org.mockito.Mockito._
import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play.{OneAppPerSuite, PlaySpec}
import play.api.test.Helpers._
import play.api.test.{FakeRequest, PlayRunners}
import com.rallyhealth.starship-activatereset.util.TestComponents
import scala.concurrent.Future

class HealthCheckControllerSpec extends PlaySpec with OneAppPerSuite with PlayRunners with MockitoSugar with OneMaterializerPerSuite {
  import scala.concurrent.ExecutionContext.Implicits.global
  override implicit lazy val app = new TestComponents().application
  val service = mock[HealthCheckService]
  val controller = new HealthCheckControllerImpl(service)(global)
  class TestData {
    private val start = System.currentTimeMillis()
    val resp: HealthCheckResponse = HealthCheckResponse(
      status = HealthCheckStatus.Success,
      serviceName = "someServiceName",
      environment = "someEnvName",
      version = "someVersion",
      duration = new Duration(System.currentTimeMillis() - start).getMillis)
  }
  class FailTestData {
    private val start = System.currentTimeMillis()
    val resp: HealthCheckResponse = HealthCheckResponse(
      status = HealthCheckStatus.Failure,
      serviceName = "someServiceName",
      environment = "someEnvName",
      version = "someVersion",
      duration = new Duration(System.currentTimeMillis() - start).getMillis)
  }
  "Health check endpoint" should {
    "return valid response if successful" in new TestData {
      when(service.monitorPlay()).thenReturn(Future.successful(resp))
      val result = controller.monitorPlay(FakeRequest())
      status(result) mustBe 200
      val resultData = contentAsJson(result).validate[HealthCheckResponse].get
      assert(resultData === resp)
    }
    "return 503 if status failure" in new FailTestData {
      when(service.monitorPlay()).thenReturn(Future.successful(resp))
      val result = controller.monitorPlay(FakeRequest())
      status(result) mustBe 503
    }
    "return 500 if exception" in new TestData {
      when(service.monitorPlay()).thenReturn(Future.failed(new RuntimeException))
      val result = controller.monitorPlay(FakeRequest())
      status(result) mustBe 500
    }
  }
}
