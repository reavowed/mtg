import {useContext} from "preact/hooks";
import GameState from "../../../GameState";
import ScreenCenter from "../../layout/ScreenCenter";
import Hand from "../Hand";
import FirstPlayerMessage from "./FirstPlayerMessage";
import MulliganDecision from "./MulliganDecision";
import OpponentMulliganMessage from "./OpponentMulliganMessage";


export default function OpeningHand() {
    const gameState = useContext(GameState);
    const decision = gameState.currentChoice.playerToAct !== gameState.player ? null :
        gameState.currentChoice.type === "MulliganChoice" ? <MulliganDecision /> :
        null;
    return <ScreenCenter>
        <div>
            <FirstPlayerMessage />
            <Hand />
            <OpponentMulliganMessage />
            {decision}
        </div>
    </ScreenCenter>;
}
