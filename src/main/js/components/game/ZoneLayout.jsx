import _ from "lodash";
import {useContext, useEffect} from "preact/hooks";
import CanvasManager from "../../contexts/CanvasManager";
import GameState from "../../contexts/GameState";
import Graveyard from "./Graveyard";
import Battlefield from "./layout/Battlefield";
import CardRow from "./card/CardRow";
import Hand from "./Hand";
import Stops from "./layout/Stops";
import Library from "./Library";
import PlayerLifeTotal from "./PlayerLifeTotal";
import Stack from "./Stack";

export default function ZoneLayout() {
    const gameState = useContext(GameState);
    const canvasManager = useContext(CanvasManager);

    const player = gameState.player;
    const opponent = _.find(gameState.gameData.playersInTurnOrder, p => p !== player);

    const creatures = _.flatMap(gameState.battlefield);

    // Show blocker lines
    useEffect(() => {
        const lines = _.flatMap(creatures, creature => {
            if (creature.modifiers && creature.modifiers.blocking) {
                return [[creature.objectId, creature.modifiers.blocking]];
            } else {
                return [];
            }
        });
        canvasManager.setLines(lines);
        return () => canvasManager.setLines([]);
    }, [gameState]);

    return <div className="d-flex flex-column h-100">
        <div className="flex-grow-1 border-bottom">
            <div class="d-flex h-100">
                <div className="flex-grow-1">
                    <div className="h-100">
                        <div className="h-50 border-bottom d-flex flex-column justify-content-start position-relative">
                            <div className="flex-grow-1">
                                <div className="d-flex h-100 ml-2">
                                    <div className="flex-grow-0">
                                        <div className="d-flex flex-column h-100">
                                            <Graveyard className="flex-grow-0 mb-2" player={opponent}/>
                                            <Library className="flex-grow-0 mb-2" player={opponent}/>
                                            <div className="flex-grow-1"/>
                                        </div>
                                    </div>
                                    <div className="flex-grow-1">
                                        <Battlefield player={opponent} direction="top" />
                                    </div>
                                </div>
                            </div>
                            <div className="flex-grow-0">
                                <div className="d-flex align-items-end">
                                    <PlayerLifeTotal direction="top" player={opponent}/>
                                    <div className="mb-2"><Stops player={opponent} direction="top"/></div>
                                </div>
                            </div>
                        </div>
                        <div className="h-50 d-flex flex-column justify-content-end position-relative">
                            <div className="flex-grow-0">
                                <div className="d-flex align-items-start">
                                    <PlayerLifeTotal direction="bottom" player={player}/>
                                    <div className="mt-2"><Stops player={player} direction="bottom"/></div>
                                </div>
                            </div>
                            <div className="flex-grow-1">
                                <div className="d-flex h-100 ml-2">
                                    <div className="flex-grow-0">
                                        <div className="d-flex flex-column h-100">
                                            <div className="flex-grow-1"/>
                                            <Library className="flex-grow-0 mb-2" player={player}/>
                                            <Graveyard className="flex-grow-0 mb-2" player={player}/>
                                        </div>
                                    </div>
                                    <div className="flex-grow-1">
                                        <Battlefield player={player} direction="bottom" />
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                <div>
                    <Stack />
                </div>
            </div>
        </div>
        <Hand className="my-2" />
    </div>
}
