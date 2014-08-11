//     ____                _       __               
//    / __ )____  _____   | |     / /___ ___________
//   / __  / __ \/ ___/   | | /| / / __ `/ ___/ ___/
//  / /_/ / /_/ (__  )    | |/ |/ / /_/ / /  (__  ) 
// /_____/\____/____/     |__/|__/\__,_/_/  /____/  
//                                              
//       A futuristic real-time strategy game.
//          This file is part of Bos Wars.
//
/**@name mainloop.cpp - The main game loop. */
//
//      (c) Copyright 1998-2008 by Lutz Sammer and Jimmy Salmon
//
//      This program is free software; you can redistribute it and/or modify
//      it under the terms of the GNU General Public License as published by
//      the Free Software Foundation; only version 2 of the License.
//
//      This program is distributed in the hope that it will be useful,
//      but WITHOUT ANY WARRANTY; without even the implied warranty of
//      MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//      GNU General Public License for more details.
//
//      You should have received a copy of the GNU General Public License
//      along with this program; if not, write to the Free Software
//      Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
//      02111-1307, USA.
//

//@{

//----------------------------------------------------------------------------
//  Includes
//----------------------------------------------------------------------------

#include <string.h>
#include <stdio.h>
#include <stdlib.h>
#include <fstream>
#include <sstream>
#include <ctime>
#include <time.h>
#include <iomanip>
#if defined(DEBUG)
#include <setjmp.h>
#endif

#include "stratagus.h"
#include "video.h"
#include "map.h"
#include "font.h"
#include "sound.h"
#include "sound_server.h"
#include "unitsound.h"
#include "unittype.h"
#include "player.h"
#include "unit.h"
#include "cursor.h"
#include "minimap.h"
#include "actions.h"
#include "missile.h"
#include "interface.h"
#include "menus.h"
#include "network.h"
#include "ui.h"
#include "unit.h"
#include "trigger.h"
#include "results.h"
#include "settings.h"
#include "replay.h"
#include "pathfinder.h"
#include "editor.h"
#include "sound.h"
#include "script.h"
#include "particle.h"
#include "cpptempl.h"

#include <guichan.h>
void DrawGuichanWidgets();

//----------------------------------------------------------------------------
// Variables
//----------------------------------------------------------------------------

	/// variable set when we are scrolling via keyboard
int KeyScrollState = ScrollNone;

	/// variable set when we are scrolling via mouse
int MouseScrollState = ScrollNone;
std::clock_t templateClock;
std::clock_t firstClock;

EventCallback GameCallbacks;   /// Game callbacks
EventCallback EditorCallbacks; /// Editor callbacks

//----------------------------------------------------------------------------
// Functions
//----------------------------------------------------------------------------

/**
**  Handle scrolling area.
**
**  @param state  Scroll direction/state.
**  @param fast   Flag scroll faster.
**
**  @todo  Support dynamic acceleration of scroll speed.
**  @todo  If the scroll key is longer pressed the area is scrolled faster.
*/
void DoScrollArea(int state, bool fast)
{
	CViewport *vp;
	int stepx;
	int stepy;
	static int remx = 0; // FIXME: docu
	static int remy = 0; // FIXME: docu

	if (state == ScrollNone) {
		return;
	}

	vp = UI.SelectedViewport;

	if (fast) {
		stepx = vp->MapWidth / 2 * TileSizeX * FRAMES_PER_SECOND;
		stepy = vp->MapHeight / 2 * TileSizeY * FRAMES_PER_SECOND;
	} else {// dynamic: let these variables increase upto fast..
		// FIXME: pixels per second should be configurable
		stepx = TileSizeX * FRAMES_PER_SECOND;
		stepy = TileSizeY * FRAMES_PER_SECOND;
	}
	if ((state & (ScrollLeft | ScrollRight)) &&
			(state & (ScrollLeft | ScrollRight)) != (ScrollLeft | ScrollRight)) {
		stepx = stepx * 100 * 100 / VideoSyncSpeed / FRAMES_PER_SECOND / (SkipFrames + 1);
		remx += stepx - (stepx / 100) * 100;
		stepx /= 100;
		if (remx > 100) {
			++stepx;
			remx -= 100;
		}
	} else {
		stepx = 0;
	}
	if ((state & (ScrollUp | ScrollDown)) &&
			(state & (ScrollUp | ScrollDown)) != (ScrollUp | ScrollDown)) {
		stepy = stepy * 100 * 100 / VideoSyncSpeed / FRAMES_PER_SECOND / (SkipFrames + 1);
		remy += stepy - (stepy / 100) * 100;
		stepy /= 100;
		if (remy > 100) {
			++stepy;
			remy -= 100;
		}
	} else {
		stepy = 0;
	}

	if (state & ScrollUp) {
		stepy = -stepy;
	}
	if (state & ScrollLeft) {
		stepx = -stepx;
	}
	vp->Set(vp->MapX, vp->MapY, vp->OffsetX + stepx, vp->OffsetY + stepy);

	// This recalulates some values
	HandleMouseMove(CursorX, CursorY);
}

/**
**  Draw map area
*/
void DrawMapArea(void)
{
	// Draw all of the viewports
	for (CViewport *vp = UI.Viewports; vp < UI.Viewports + UI.NumViewports; ++vp) {
		// Center viewport on tracked unit
		if (vp->Unit) {
			if (vp->Unit->Destroyed ||
					vp->Unit->Orders[0]->Action == UnitActionDie) {
				vp->Unit = NoUnitP;
			} else {
				vp->Center(vp->Unit->X, vp->Unit->Y,
					vp->Unit->IX + TileSizeX / 2, vp->Unit->IY + TileSizeY / 2);
			}
		}

		vp->Draw();
	}
}

/**
**  Display update.
**
**  This functions updates everything on screen. The map, the gui, the
**  cursors.
*/
void UpdateDisplay(void)
{
	if (GameRunning || Editor.Running == EditorEditing) {
		DrawMapArea();
		DrawMessages();

		if (CursorState == CursorStateRectangle) {
			DrawCursor();
		}

		if (!BigMapMode) {
			for (int i = 0; i < (int)UI.Fillers.size(); ++i) {
				UI.Fillers[i].G->DrawClip(UI.Fillers[i].X, UI.Fillers[i].Y);
			}
			DrawMenuButtonArea();

			UI.Minimap.Draw(UI.SelectedViewport->MapX, UI.SelectedViewport->MapY);
			UI.Minimap.DrawCursor(UI.SelectedViewport->MapX,
				UI.SelectedViewport->MapY);

			UI.InfoPanel.Draw();
			UI.ButtonPanel.Draw();
			DrawResources();
			UI.StatusLine.Draw();
		}

		DrawCosts();
		DrawTimer();
	}

	DrawPieMenu(); // draw pie menu only if needed

	DrawGuichanWidgets();
	
	if (CursorState != CursorStateRectangle) {
		DrawCursor();
	}

	//
	// Update changes to display.
	//
	Invalidate();
}

static void InitGameCallbacks(void)
{
	GameCallbacks.ButtonPressed = HandleButtonDown;
	GameCallbacks.ButtonReleased = HandleButtonUp;
	GameCallbacks.MouseMoved = HandleMouseMove;
	GameCallbacks.MouseExit = HandleMouseExit;
	GameCallbacks.KeyPressed = HandleKeyDown;
	GameCallbacks.KeyReleased = HandleKeyUp;
	GameCallbacks.KeyRepeated = HandleKeyRepeat;
	GameCallbacks.NetworkEvent = NetworkEvent;
}

/**
**  Game main loop.
**
**  Unit actions.
**  Missile actions.
**  Players (AI).
**  Cyclic events (color cycle,...)
**  Display update.
**  Input/Network/Sound.
*/
void GameMainLoop(void)
{
	int player;
	const EventCallback *old_callbacks;

	InitGameCallbacks();

	old_callbacks = GetCallbacks();
	SetCallbacks(&GameCallbacks);

	SetVideoSync();
	GameCursor = UI.Point.Cursor;
	GameRunning = true;

	CParticleManager::init();

	MultiPlayerReplayEachCycle();

	CclCommand("GameStarting()");

	std::ofstream output;
	std::wstring templ;
	firstClock = std::clock();
        templateClock = 0;
	int messageNumber = 1;
	std::ofstream outputEventsPerSec;
	std::ofstream outputNT;
	outputEventsPerSec.open("/tmp/boswarseventspersec.csv",std::ios::out);
   	outputNT.open("/tmp/boswarsclocksperevents.csv",std::ios::out);

	output.open("/tmp/beepbeep.fifo",std::ios::out);

	templ = templ = L"<message>\n"
				L"  <units>\n"
				    L"{% for unit in units.tabunits %}"
				    L"  <unit>\n"
				    L"    <id>{$unit.id}</id>\n"
				    L"    <type>{$unit.type}</type>\n"
				    L"    <isbuilding>{$unit.isbuilding}</isbuilding>\n"
				    L"    <player>{$unit.player}</player>\n"
				    L"    <order>\n"
				    L"      <action>{$unit.action}</action>\n"
				    L"      <goal>{$unit.goal}</goal>\n"
				    L"    </order>\n"
				    L"    <neworder>\n"
				    L"       <action>{$unit.neworder}</action>\n"
				    L"       <goal>{$unit.newgoal}</goal>\n"
				    L"    </neworder>\n"
				    L"  </unit>\n"
				    L"{% endfor %}"
             		   	L"  </units>\n"
				L"<overhead>{$overhead}</overhead>\n"
            		L"</message>\n"; 

	

	while (GameRunning) {

		// Can't find a better place.
		SaveGameLoading = false;
		//
		// Game logic part
		//
		if (!GamePaused && NetworkInSync && !SkipGameCycle) {
			SinglePlayerReplayEachCycle();
			++GameCycle;
			MultiPlayerReplayEachCycle();
			NetworkCommands();  // Get network commands
			UnitActions();      // handle units
			MissileActions();   // handle missiles
			PlayersEachCycle(); // handle players
			UpdateTimer();      // update game timer

			//
			// Work todo each second.
			// Split into different frames, to reduce cpu time.
			// Increment mana of magic units.
			// Update mini-map.
			// Update map fog of war.
			// Call AI.
			// Check game goals.
			// Check rescue of units.
			//
			switch (GameCycle % CYCLES_PER_SECOND) {
				case 0: // At cycle 0, start all ai players...
					if (GameCycle == 0) {
						for (player = 0; player < NumPlayers; ++player) {
							PlayersEachSecond(player);
						}
					}
					break;
				case 1:
					break;
				case 2:
					break;
				case 3: // minimap update
					UI.Minimap.Update();
					break;
				case 4:
					break;
				case 5:
					break;
				case 6: // overtaking units
					RescueUnits();
					break;
				default:
					// FIXME: assume that NumPlayers < (CYCLES_PER_SECOND - 7)
					player = (GameCycle % CYCLES_PER_SECOND) - 7;
					Assert(player >= 0);
					if (player < NumPlayers) {
						PlayersEachSecond(player);
					}
			}

			////////////GET INFORMATION FOR MONITORING!!!////////////////////
			
						

			if(NumUnits > 0)
			{
				std::clock_t littleClock = std::clock();
	
				cpptempl::data_list theUnits;
		    
		    		std::stringstream ss;

				for(int i = 0; i < NumUnits; i++)
				{
					cpptempl::data_map uni;

					/////////ID/////////
					ss.str("");
					ss << Units[i]->Slot;
					std::string theID = ss.str();
					std::wstring wTheID;
					wTheID.assign(theID.begin(),theID.end());
					uni[L"id"] = cpptempl::make_data(wTheID);

					//////UNITTYPE//////
					ss.str("");
					ss << Units[i]->Type->Name;
					std::string theType = ss.str();
					std::wstring wTheType;
					wTheType.assign(theType.begin(),theType.end());
					uni[L"type"] = cpptempl::make_data(wTheType);

					//////ISBUILDING//////
					ss.str("");
					if (Units[i]->Type->Building == 1)
						ss << "true";
					else
						ss << "false";
					std::string theIsBuilding = ss.str();
					std::wstring wTheIsBuilding;
					wTheIsBuilding.assign(theIsBuilding.begin(),theIsBuilding.end());
					uni[L"isbuilding"] = cpptempl::make_data(wTheIsBuilding);

					//////PLAYER//////
					ss.str("");
					ss << Units[i]->Player;
					std::string thePlayer = ss.str();
					std::wstring wThePlayer;
					wThePlayer.assign(thePlayer.begin(),thePlayer.end());
					uni[L"player"] = cpptempl::make_data(wThePlayer);

					//////UnitAction//////
					ss.str("");
					switch(Units[i]->Orders[0]->Action)
					{
					  	case UnitActionNone: ss << "UnitActionNone"; break;
						case UnitActionStill: ss << "UnitActionStill"; break;  
						case UnitActionStandGround: ss << "UnitActionStandGround"; break;
						case UnitActionFollow: ss << "UnitActionFollow"; break;    
						case UnitActionMove: ss << "UnitActionMove"; break;      
						case UnitActionAttack: ss << "UnitActionAttack"; break;   
						case UnitActionAttackGround: ss << "UnitActionAttackGround"; break;
						case UnitActionDie: ss << "UnitActionDie"; break;     

						case UnitActionSpellCast: ss << "UnitActionSpellCast"; break; 

						case UnitActionTrain: ss << "UnitActionTrain"; break;  
						case UnitActionBuilt: ss << "UnitActionBuilt"; break;   
						case UnitActionBoard: ss << "UnitActionBoard"; break;    
						case UnitActionUnload: ss << "UnitActionUnload"; break; 
						case UnitActionPatrol: ss << "UnitActionPatrol"; break; 
						case UnitActionBuild: ss << "UnitActionBuild"; break;

						case UnitActionRepair: ss << "UnitActionRepair"; break; 
						case UnitActionResource: ss << "UnitActionResource"; break;
						default: ss << "None";  
					}

					std::string theAction = ss.str();
					std::wstring wTheAction;
					wTheAction.assign(theAction.begin(),theAction.end());
					uni[L"action"] = cpptempl::make_data(wTheAction);

					//////UnitGoal//////
					ss.str("");
					if(Units[i]->Orders[0]->Goal != NULL)
						ss << Units[i]->Orders[0]->Goal->Slot;
					else
						ss << "None";
					std::string theGoal = ss.str();
					std::wstring wTheGoal;
					wTheGoal.assign(theGoal.begin(),theGoal.end());
					uni[L"goal"] = cpptempl::make_data(wTheGoal);

					///////NEW ORDER/////////
					ss.str("");
					switch(Units[i]->NewOrder.Action)
					{
					  	case UnitActionNone: ss << "UnitActionNone"; break;
						case UnitActionStill: ss << "UnitActionStill"; break;  
						case UnitActionStandGround: ss << "UnitActionStandGround"; break;
						case UnitActionFollow: ss << "UnitActionFollow"; break;    
						case UnitActionMove: ss << "UnitActionMove"; break;      
						case UnitActionAttack: ss << "UnitActionAttack"; break;   
						case UnitActionAttackGround: ss << "UnitActionAttackGround"; break;
						case UnitActionDie: ss << "UnitActionDie"; break;     

						case UnitActionSpellCast: ss << "UnitActionSpellCast"; break; 

						case UnitActionTrain: ss << "UnitActionTrain"; break;  
						case UnitActionBuilt: ss << "UnitActionBuilt"; break;   
						case UnitActionBoard: ss << "UnitActionBoard"; break;    
						case UnitActionUnload: ss << "UnitActionUnload"; break; 
						case UnitActionPatrol: ss << "UnitActionPatrol"; break; 
						case UnitActionBuild: ss << "UnitActionBuild"; break;

						case UnitActionRepair: ss << "UnitActionRepair"; break; 
						case UnitActionResource: ss << "UnitActionResource"; break;
						default: ss << "None";  
					}

					std::string theNewOrder = ss.str();
					std::wstring wTheNewOrder;
					wTheNewOrder.assign(theNewOrder.begin(),theNewOrder.end());
					uni[L"neworder"] = cpptempl::make_data(wTheNewOrder);

					//////NEW GOAL//////
					ss.str("");
					if(Units[i]->NewOrder.Goal != NULL)
						ss << Units[i]->NewOrder.Goal->Slot;
					else
						ss << "None";
					std::string theNewGoal = ss.str();
					std::wstring wTheNewGoal;
					wTheNewGoal.assign(theNewGoal.begin(),theNewGoal.end());
					uni[L"newgoal"] = cpptempl::make_data(wTheNewGoal);
			
					theUnits.push_back((cpptempl::make_data(uni)));
				}
				

				cpptempl::data_map lesUnits;
		    		lesUnits[L"tabunits"] = cpptempl::make_data(theUnits);

				// Now set this in the data map
				cpptempl::data_map data;
				data[L"units"] = cpptempl::make_data(lesUnits);

				//////////////////TIMESTAMP/////////////////////////
				std::clock_t t = std::clock() - firstClock;
				double timeStamp = ((double)t/(double)CLOCKS_PER_SEC);
				ss.str("");
				ss << std::setprecision(8) << timeStamp;
				std::string sTimeStamp = ss.str();
				std::string gnuPlotStringT = ss.str();
				std::wstring wTimeStamp;
				wTimeStamp.assign(sTimeStamp.begin(),sTimeStamp.end());
				data[L"timestamp"] = cpptempl::make_data(wTimeStamp);

				///OVERHEAD/////
				templateClock += std::clock()-littleClock;
				long double overhead = (long double)templateClock / ((long double)std::clock()-(long double)firstClock)*(long double)100.0;
				ss.str("");
				ss << std::setprecision(10) << overhead;
				std::string sOverhead = ss.str();
				std::wstring wOverhead;
				wOverhead.assign(sOverhead.begin(),sOverhead.end());
				data[L"overhead"] = cpptempl::make_data(wOverhead);
				std::cout << overhead << "% overhead\n";
				//std::cout << std::clock() << " clocl\n";

				/////////////////MESSAGENUMBER/////////////////////////<
			        ss.str("");
			        ss << messageNumber;
			        std::string gnuPlotStringN = ss.str();
			        std::string sMessageNumber = ss.str();
			        std::wstring wMessageNumber;
			        wMessageNumber.assign(sMessageNumber.begin(),sMessageNumber.end());
			        data[L"messagenumber"] = cpptempl::make_data(wMessageNumber);

				std::wstring result = cpptempl::parse(templ, data);
				
				ss.str("");
				
				double templateTime = ((double)templateClock/(double)CLOCKS_PER_SEC)*(double)1000;
				ss << std::setprecision(8) << templateTime;
				std::string templTime = ss.str();				
				
				double totalT = ((std::clock()-firstClock)/(double)CLOCKS_PER_SEC);
				std::cout << totalT << "totaltime\n";
				std::cout << CYCLES_PER_SECOND << "cycles\n";
				std::string s;
		    		s.assign(result.begin(), result.end());
				
				if (messageNumber == 1)
				{
					std::ofstream traceoutput;
					traceoutput.open("/tmp/traceout.xml",std::ios::out);
					traceoutput << s;
					traceoutput.close();
				}

				messageNumber++;

				/////STATS/////
								

				output << s;
		
				std::string out = gnuPlotStringN + "," + gnuPlotStringT + "\n";
				outputEventsPerSec << out;
		
				//OutputNT
				//std::string theT;
				out = "";
				//ss.str("");
				//ss << templTime;
				//theT = ss.str();
				out = templTime + "," + gnuPlotStringN + "\n";
				outputNT << out;
				
			}
			/////////////////////////////////////////////////////////////////
		}

		TriggersEachCycle();      // handle triggers
		UpdateMessages();         // update messages
		ParticleManager.update(); // handle particles

		CheckMusicFinished();     // Check for next song

		//
		// Map scrolling
		//
		DoScrollArea(MouseScrollState | KeyScrollState, (KeyModifiers & ModifierControl) != 0);

		if (FastForwardCycle <= GameCycle || GameCycle <= 10 || !(GameCycle & 0x3f)) {
			//FIXME: this might be better placed somewhere at front of the
			// program, as we now still have a game on the background and
			// need to go through the game-menu or supply a map file
			UpdateDisplay();

			//
			// If double-buffered mode, we will display the contains of
			// VideoMemory. If direct mode this does nothing. In X11 it does
			// XFlush
			//
			RealizeVideoMemory();
		}

		if (FastForwardCycle <= GameCycle || !(GameCycle & 0x3f)) {
			WaitEventsOneFrame();
		}
		if (!NetworkInSync) {
			NetworkRecover(); // recover network
		}

		
	}

	output.close();
	outputNT.close();
    	outputEventsPerSec.close();
	//
	// Game over
	//
	NetworkQuit();
	EndReplayLog();

	CParticleManager::exit();

	FlagRevealMap = 0;
	ReplayRevealMap = 0;
	GamePaused = false;
	GodMode = false;

	SetCallbacks(old_callbacks);
}

//@}
