package mtg.web

import mtg.core.PlayerId
import mtg.game.turns.TurnPhase
import mtg.web.visibleState.VisibleState
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.{HttpStatus, ResponseEntity}
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation._

@Controller
class GameController @Autowired() (gameService: GameService) {

  @GetMapping(Array("/{playerIdentifier}"))
  def viewGame(@PathVariable("playerIdentifier") playerIdentifier: String): ResponseEntity[_] = {
    val html = "<!doctype html>" +
      <html>
        <head>
          <title>Free MTG</title>
          <link href="/assets/main.css" rel="stylesheet"></link>
        </head>
        <body>
          <div id="container"/>
          <script crossorigin="" src="/assets/main.js"></script>
        </body>
      </html>.toString
      new ResponseEntity[String](html, HttpStatus.OK)
  }

  @GetMapping(Array("/{playerIdentifier}/state"))
  @ResponseBody
  def getState(@PathVariable("playerIdentifier") playerIdentifier: String) = {
    VisibleState.forPlayer(PlayerId(playerIdentifier), gameService.gameStateManager.gameState)
  }

  @PostMapping(Array("/{playerIdentifier}/decision"))
  @ResponseBody
  def makeDecision(@PathVariable("playerIdentifier") playerIdentifier: String, @RequestBody(required = false) decision: String) = {
    gameService.gameStateManager.handleDecision(if (decision == null) "" else decision, PlayerId(playerIdentifier))
  }

  @PostMapping(Array("/{playerIdentifier}/requestUndo"))
  @ResponseBody
  def requestUndo(@PathVariable("playerIdentifier") playerIdentifier: String) = {
    gameService.gameStateManager.requestUndo(PlayerId(playerIdentifier))
  }

  @GetMapping(value = Array("/{playerIdentifier}/stops"))
  @ResponseBody
  def getStops(@PathVariable("playerIdentifier") playerIdentifier: String) = {
    gameService.gameStateManager.stops(PlayerId(playerIdentifier))
  }

  @PostMapping(value = Array("/{playerIdentifier}/stops/{playerToStopAt}/{stepOrPhase}"))
  @ResponseBody
  def setStop(
    @PathVariable("playerIdentifier") playerIdentifier: String,
    @PathVariable("playerToStopAt") playerToStopAt: String,
    @PathVariable("stepOrPhase") stepOrPhaseText: String
  ) = {
    val stepOrPhaseOption = TurnPhase.AllPhasesAndSteps.find(_.name == stepOrPhaseText)
    stepOrPhaseOption.foreach(stepOrPhase => {
      gameService.gameStateManager.setStop(PlayerId(playerIdentifier), PlayerId(playerToStopAt), stepOrPhase)
    })
    gameService.gameStateManager.stops(PlayerId(playerIdentifier))
  }

  @DeleteMapping(value = Array("/{playerIdentifier}/stops/{playerToStopAt}/{stepOrPhase}"))
  @ResponseBody
  def unsetStop(
    @PathVariable("playerIdentifier") playerIdentifier: String,
    @PathVariable("playerToStopAt") playerToStopAt: String,
    @PathVariable("stepOrPhase") stepOrPhaseText: String
  ) = {
    val stepOrPhaseOption = TurnPhase.AllPhasesAndSteps.find(_.name == stepOrPhaseText)
    stepOrPhaseOption.foreach(stepOrPhase => {
      gameService.gameStateManager.unsetStop(PlayerId(playerIdentifier), PlayerId(playerToStopAt), stepOrPhase)
    })
    gameService.gameStateManager.stops(PlayerId(playerIdentifier))
  }
}
