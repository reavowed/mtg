import {useContext} from "preact/hooks";
import GameState from "../../../GameState";
import ScreenCenter from "../../layout/ScreenCenter";
import Hand from "../Hand";
import MulliganChoice from "./MulliganChoice";
import ReturnCardsToLibraryChoice from "./ReturnCardsToLibraryChoice";

function OpeningHandWhileWaiting() {
    return <div>
        <Hand />
        <h1 className="text-center mt-3">Waiting for your opponent to make a mulligan decision</h1>
    </div>
}

export default function OpeningHand() {
    const gameState = useContext(GameState);
    const decision = gameState.currentChoice.playerToAct !== gameState.player ? <OpeningHandWhileWaiting/> :
        gameState.currentChoice.type === "MulliganChoice" ? <MulliganChoice /> :
        gameState.currentChoice.type === "ReturnCardsToLibraryChoice" ? <ReturnCardsToLibraryChoice /> :
        <Hand />;
    return <ScreenCenter>
        <div className="w-100">
            {decision}
        </div>
    </ScreenCenter>;
}
