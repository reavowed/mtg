import {useContext} from "preact/hooks";
import GameState from "../../../GameState";
import ScreenCenter from "../../layout/ScreenCenter";
import Hand from "../Hand";
import FirstPlayerMessage from "./FirstPlayerMessage";
import MulliganChoice from "./MulliganChoice";
import OpponentMulliganMessage from "./OpponentMulliganMessage";
import ReturnCardsToLibraryChoice from "./ReturnCardsToLibraryChoice";


export default function OpeningHand() {
    const gameState = useContext(GameState);
    const decision = gameState.currentChoice.playerToAct !== gameState.player ? <Hand /> :
        gameState.currentChoice.type === "MulliganChoice" ? <MulliganChoice /> :
        gameState.currentChoice.type === "ReturnCardsToLibraryChoice" ? <ReturnCardsToLibraryChoice /> :
        <Hand />;
    return <ScreenCenter>
        <div>
            <FirstPlayerMessage />
            <OpponentMulliganMessage />
            {decision}
        </div>
    </ScreenCenter>;
}
