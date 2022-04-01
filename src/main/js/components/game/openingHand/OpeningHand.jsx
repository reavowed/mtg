import {useContext} from "preact/hooks";
import GameState from "../../../contexts/GameState";
import BannerText from "../../layoutUtils/BannerText";
import ScreenCenter from "../../layoutUtils/ScreenCenter";
import CardWithText from "../card/CardWithText";
import Hand from "../Hand";
import MulliganChoice from "./MulliganChoice";
import ReturnCardsToLibraryChoice from "./ReturnCardsToLibraryChoice";

function OpeningHandWhileWaiting() {
    return <div>
        <Hand as={CardWithText} />
        <BannerText>Waiting for your opponent to make a mulligan decision</BannerText>
    </div>
}

export default function OpeningHand() {
    const gameState = useContext(GameState);
    const decision = gameState.currentChoice.playerToAct !== gameState.player ? <OpeningHandWhileWaiting/> :
        gameState.currentChoice.type === "MulliganChoice" ? <MulliganChoice /> :
        gameState.currentChoice.type === "ChooseCardsInHand" ? <ReturnCardsToLibraryChoice /> :
        <Hand as={CardWithText} />;
    return <ScreenCenter>
        <div className="w-100">
            {decision}
        </div>
    </ScreenCenter>;
}
