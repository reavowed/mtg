package mtg.web

import mtg.game.PlayerIdentifier
import mtg.game.turns.{TurnPhase, TurnPhaseWithSteps, TurnStep}
import mtg.utils.CaseObjectSerializer
import mtg.web.visibleState.VisibleState
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.{HttpStatus, ResponseEntity}
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.{DeleteMapping, GetMapping, PathVariable, PostMapping, RequestBody, ResponseBody}

import scala.xml.Unparsed

@Controller
class GameController @Autowired() (gameService: GameService) {

  @GetMapping(Array("/{playerIdentifier}"))
  def viewGame(@PathVariable("playerIdentifier") playerIdentifier: String): ResponseEntity[_] = {
    val html = "<!doctype html>" +
      <html>
        <head>
          <title>Free MTG</title>
          <link href="/css/main.css" rel="stylesheet"></link>
        </head>
        <body>
          <div id="container"/>
          <script crossorigin="" src="/js/main.js"></script>
        </body>
      </html>.toString
      new ResponseEntity[String](html, HttpStatus.OK)
  }

  @GetMapping(Array("/{playerIdentifier}/state"))
  @ResponseBody
  def getState(@PathVariable("playerIdentifier") playerIdentifier: String) = {
    VisibleState.forPlayer(PlayerIdentifier(playerIdentifier), gameService.gameStateManager.currentGameState)
  }

  @PostMapping(Array("/{playerIdentifier}/decision"))
  @ResponseBody
  def makeDecision(@PathVariable("playerIdentifier") playerIdentifier: String, @RequestBody decision: String) = {
    gameService.gameStateManager.handleDecision(decision, PlayerIdentifier(playerIdentifier))
  }

  @GetMapping(value = Array("/{playerIdentifier}/stops"))
  @ResponseBody
  def getStops(@PathVariable("playerIdentifier") playerIdentifier: String) = {
    gameService.gameStateManager.stops(PlayerIdentifier(playerIdentifier))
  }

  @PostMapping(value = Array("/{playerIdentifier}/stops/{playerToStopAt}/{stepOrPhase}"))
  @ResponseBody
  def setStop(
    @PathVariable("playerIdentifier") playerIdentifier: String,
    @PathVariable("playerToStopAt") playerToStopAt: String,
    @PathVariable("stepOrPhase") stepOrPhaseText: String
  ) = {
    val stepOrPhaseOption = TurnPhase.AllPhasesAndSteps.find(CaseObjectSerializer.getClassName(_) == stepOrPhaseText)
    stepOrPhaseOption.foreach(stepOrPhase => {
      gameService.gameStateManager.stops
        .updateWith(
          PlayerIdentifier(playerIdentifier))(
          _.map(_.updatedWith(
            PlayerIdentifier(playerToStopAt))(
            _.map(existingStops => if (existingStops.contains(stepOrPhase)) existingStops else existingStops :+ stepOrPhase))))
    })
    gameService.gameStateManager.stops(PlayerIdentifier(playerIdentifier))
  }

  @DeleteMapping(value = Array("/{playerIdentifier}/stops/{playerToStopAt}/{stepOrPhase}"))
  @ResponseBody
  def unsetStop(
    @PathVariable("playerIdentifier") playerIdentifier: String,
    @PathVariable("playerToStopAt") playerToStopAt: String,
    @PathVariable("stepOrPhase") stepOrPhaseText: String
  ) = {
    val stepOrPhaseOption = TurnPhase.AllPhasesAndSteps.find(CaseObjectSerializer.getClassName(_) == stepOrPhaseText)
    stepOrPhaseOption.foreach(stepOrPhase => {
      gameService.gameStateManager.stops
        .updateWith(
          PlayerIdentifier(playerIdentifier))(
          _.map(_.updatedWith(
            PlayerIdentifier(playerToStopAt))(
            _.map(existingStops => existingStops.filter(_ != stepOrPhase)))))
    })
    gameService.gameStateManager.stops(PlayerIdentifier(playerIdentifier))
  }
}
