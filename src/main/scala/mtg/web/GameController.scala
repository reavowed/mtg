package mtg.web

import mtg.game.PlayerIdentifier
import mtg.web.visibleState.VisibleState
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.{HttpStatus, ResponseEntity}
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.{GetMapping, PathVariable, ResponseBody}

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
}
