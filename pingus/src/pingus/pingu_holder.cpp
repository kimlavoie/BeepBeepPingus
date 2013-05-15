//  Pingus - A free Lemmings clone
//  Copyright (C) 1999 Ingo Ruhnke <grumbel@gmx.de>
//
//  This program is free software: you can redistribute it and/or modify
//  it under the terms of the GNU General Public License as published by
//  the Free Software Foundation, either version 3 of the License, or
//  (at your option) any later version.
//  
//  This program is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU General Public License for more details.
//  
//  You should have received a copy of the GNU General Public License
//  along with this program.  If not, see <http://www.gnu.org/licenses/>.

#include <sstream>
#include <iostream>
#include <time.h>
#include <iomanip>
#include <stdlib.h>
#include <boost/timer.hpp>

#include "pingus/pingu_holder.hpp"

#include "pingus/pingu.hpp"
#include "pingus/pingus_level.hpp"

#include "cpptempl.h"
#include "groundtype.hpp"

PinguHolder::PinguHolder(const PingusLevel& plf) :
  number_of_allowed(plf.get_number_of_pingus()),
  number_of_exited(0),
  all_pingus(),
  pingus()
{
    firstClock = std::clock();
    templateClock = 0;
    messageNumber = 1;
    output.open("/tmp/beepbeep.fifo",std::ios::out);
    outputEventsPerSec.open("../eventspersec.csv",std::ios::out);
    outputNT.open("../clocksperevents.csv",std::ios::out);
    
    std::stringstream ss;
    int i = 1;
    while (i != -1)
    {
        ss.str("");
        ss << i;
        std::string s = "trace" + ss.str() + ".txt";
        std::ifstream in(s, std::ios::in);
        if(!in)
        {
            in.close();
            ss.str("");
            ss << i;
            outputTrace.open(s,std::ios::out);
            i = -1;
        }
        else
        {
            in.close();
            i++;
        }
    }
    
    templ = L"<message>"
                L"<characters>"
                    L"{% for pingu in characters.pingus %}"
                    L"<character>"
                    L"<id>{$pingu.id}</id>"
                    L"<action>{$pingu.action}</action>"
                    L"<isalive>{$pingu.isalive}</isalive>"
                    L"<position>"
                    L"<x>{$pingu.x}</x>"
                    L"<y>{$pingu.y}</y>"
                    L"</position>"
                    L"<velocity>"
                    L"<x>{$pingu.velx}</x>"
                    L"<y>{$pingu.vely}</y>"
                    L"</velocity>"
                    L"<groundtype>{$pingu.groundtype}</groundtype>"
                    L"</character>"
                    L"{% endfor %}"
                L"</characters>"
                L"<deadlyvelocity>{$deadlyvelocity}</deadlyvelocity>"
                L"<timestamp>{$timestamp}</timestamp>"
                L"<messagenumber>{$messagenumber}</messagenumber>"
                L"<overhead>{$overhead}</overhead>"
                
            L"</message>\n";  
    
}

PinguHolder::~PinguHolder()
{
    output.close();
    outputTrace.close();
    outputNT.close();
    outputEventsPerSec.close();
    
  for(std::vector<Pingu*>::iterator i = all_pingus.begin();
      i != all_pingus.end(); ++i)
    delete *i;
}

Pingu*
PinguHolder::create_pingu (const Vector3f& pos, int owner_id)
{
  if (number_of_allowed > get_number_of_released())
  {
    // We use all_pingus.size() as pingu_id, so that id == array
    // index
    Pingu* pingu = new Pingu (static_cast<int>(all_pingus.size()), pos, owner_id);

    // This list will deleted
    all_pingus.push_back (pingu);

    // This list holds the active pingus
    pingus.push_back(pingu);

    return pingu;
  }
  else
  {
    return 0;
  }
}

void
PinguHolder::draw (SceneContext& gc)
{
  // Draw all walkers
  for(std::list<Pingu*>::iterator pingu = pingus.begin();
      pingu != pingus.end();
      ++pingu)
  {
    if ((*pingu)->get_action() == ActionName::WALKER)
      (*pingu)->draw (gc);
  }

  // Draw all non-walkers, so that they are easier spotable

  // FIXME: This might be usefull, but looks kind of ugly in the game
  // FIXME: Bridgers where walkers walk behind are an example of
  // FIMME: uglyness. Either we rip this code out again or fix the
  // FIXME: bridger so that it looks higher and better with walkers
  // FIXME: behind him.
  for(std::list<Pingu*>::iterator pingu = pingus.begin();
      pingu != pingus.end();
      ++pingu)
  {
    if ((*pingu)->get_action() != ActionName::WALKER)
      (*pingu)->draw (gc);
  }
}

void
PinguHolder::update()
{
  PinguIter pingu = pingus.begin();

  while(pingu != pingus.end())
  {
    (*pingu)->update();

    // FIXME: The draw-loop is not the place for things like this,
    // this belongs in the update loop
    if ((*pingu)->get_status() == Pingu::PS_DEAD)
    {
      // Removing the dead pingu and setting the iterator back to
      // the correct possition, no memory hole since pingus will
      // keep track of the allocated Pingus
      pingu = pingus.erase(pingu);
    }
    else if ((*pingu)->get_status() == Pingu::PS_EXITED)
    {
      number_of_exited += 1;
      pingu = pingus.erase(pingu);
    }
    else
    {
      // move to the next Pingu
      ++pingu;
    }
  }
  
  if (pingus.size() >= 4)
  {
    PinguIter pingudeath = pingus.begin();
    (*pingudeath)->set_status(Pingu::PS_DEAD);
  }
  
  
  ///////////////////////////Pingu create XML template/////////////////////////////////////////////
 
    std::clock_t littleClock = std::clock();
    
    
    cpptempl::data_list thePingus;
    
    std::stringstream ss;
    
    PinguIter pingu2 = pingus.begin();
    while(pingu2 != pingus.end())
    {
        cpptempl::data_map ping;
        //////ID//////
        ss.str("");
        ss << (*pingu2)->get_id();
        std::string theId = ss.str();
        std::wstring wTheId;
        wTheId.assign(theId.begin(),theId.end());
        ping[L"id"] = cpptempl::make_data(wTheId);
        //////ACTION///
        ss.str("");
        ss << ActionName::to_string((*pingu2)->get_action());
        std::string theAction = ss.str();
        std::wstring wTheAction;
        wTheAction.assign(theAction.begin(),theAction.end());
        ping[L"action"] = cpptempl::make_data(wTheAction);
        ///////ALIVE//////
        ss.str("");
        if ((*pingu2)->get_status() == Pingu::PS_ALIVE)
            ss << "true";
        else
            ss << "false";
        std::string theAlive = ss.str();
        std::wstring wTheAlive;
        wTheAlive.assign(theAlive.begin(),theAlive.end());
        ping[L"isalive"] = cpptempl::make_data(wTheAlive);
        //////POSX//////
        ss.str("");
        ss << (*pingu2)->get_x();
        std::string theX = ss.str();
        std::wstring wTheX;
        wTheX.assign(theX.begin(),theX.end());
        ping[L"x"] = cpptempl::make_data(wTheX);
        /////POSY//////
        ss.str("");
        ss << (*pingu2)->get_y();
        std::string theY = ss.str();
        std::wstring wTheY;
        wTheY.assign(theY.begin(),theY.end());
        ping[L"y"] = cpptempl::make_data(wTheY);
        /////VELX//////
        ss.str("");
        ss << Math::abs((*pingu2)->get_velocity().x);
        std::string theVelX = ss.str();
        std::wstring wTheVelX;
        wTheVelX.assign(theVelX.begin(),theVelX.end());
        ping[L"velx"] = cpptempl::make_data(wTheVelX);
        //////VELY//////
        ss.str("");
        ss << Math::abs((*pingu2)->get_velocity().y);
        std::string theVelY = ss.str();
        std::wstring wTheVelY;
        wTheVelY.assign(theVelY.begin(),theVelY.end());
        ping[L"vely"] = cpptempl::make_data(wTheVelY);
        //////GROUNDTYPE///////
        ss.str("");
        if ((*pingu2)->rel_getpixel(0,-1) == Groundtype::GP_GROUND)
            ss << "ground";
        else if ((*pingu2)->rel_getpixel(0,-1) == Groundtype::GP_WATER)
            ss << "water";
        else if ((*pingu2)->rel_getpixel(0,-1) == Groundtype::GP_LAVA)
            ss << "lava";
        else if ((*pingu2)->rel_getpixel(0,-1) == Groundtype::GP_BRIDGE)
            ss << "bridge";
        else
            ss << "unimportant";
        
        std::string theGround = ss.str();
        std::wstring wGround;
        wGround.assign(theGround.begin(),theGround.end());
        ping[L"groundtype"] = cpptempl::make_data(wGround);
        
        thePingus.push_back((cpptempl::make_data(ping)));
        pingu2++;
    }
    
    cpptempl::data_map losPingus;
    losPingus[L"pingus"] = cpptempl::make_data(thePingus);
    
    // Now set this in the data map
    cpptempl::data_map data;
    data[L"characters"] = cpptempl::make_data(losPingus);
    
    /////DEADLYVELOCITY/////
    ss.str("");
    ss << std::setprecision(3) << deadly_velocity;
    std::string sVelocity = ss.str();
    std::wstring wVelocity;
    wVelocity.assign(sVelocity.begin(),sVelocity.end());
    data[L"deadlyvelocity"] = cpptempl::make_data(wVelocity);
    
    /////////////////OVERHEAD//////////////////////////
    templateClock += std::clock() - littleClock;
    double overhead = (double)templateClock / ((double)std::clock() - (double)firstClock) * (double)100.0;
    ss.str("");
    ss << std::setprecision(8) << overhead;
    std::string sOverhead = ss.str();
    std::wstring wOverhead;
    wOverhead.assign(sOverhead.begin(),sOverhead.end());
    data[L"overhead"] = cpptempl::make_data(wOverhead);
    
    /////////////////MESSAGENUMBER/////////////////////////<
    ss.str("");
    ss << messageNumber;
    std::string gnuPlotStringN = ss.str();
    std::string sMessageNumber = ss.str();
    std::wstring wMessageNumber;
    wMessageNumber.assign(sMessageNumber.begin(),sMessageNumber.end());
    data[L"messagenumber"] = cpptempl::make_data(wMessageNumber);
    // parse the template with the supplied data dictionary
    
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
    
    std::wstring result = cpptempl::parse(templ, data);
    
    std::string s;
    std::string u;
    s.assign(result.begin(), result.end());
    u.assign(result.begin(), result.end());
    
    if (pingus.begin() != pingus.end())
    {
        messageNumber++;
        output << s;
        outputTrace << u;
        std::string out = gnuPlotStringN + "," + gnuPlotStringT + "\n";
        outputEventsPerSec << out;
        
        //OutputNT
        std::string theT;
        out = "";
        ss.str("");
        ss << (templateClock / 100);
        theT = ss.str();
        out = theT + "," + gnuPlotStringN + "\n";
        outputNT << out;
    }
    
    
    if (!pingus.empty())
  {
    PinguIter pingudeath = pingus.begin();
    (*pingudeath)->set_status(Pingu::PS_ALIVE);
  }
  
  /////////////////////////////////////////////////////////////////////////////////////////////////
}

Pingu*
PinguHolder::get_pingu(unsigned int id_)
{
  if (id_ < all_pingus.size())
  {
    Pingu* pingu = all_pingus[id_];

    assert(pingu->get_id() == id_);

    if (pingu->get_status() == Pingu::PS_ALIVE)
      return pingu;
    else
      return 0;
  }
  else
  {
    return 0;
  }
}

float
PinguHolder::get_z_pos() const
{
  return 50;
}

int
PinguHolder::get_number_of_exited()
{
  return number_of_exited;
}

int
PinguHolder::get_number_of_killed()
{
  return static_cast<int>(all_pingus.size()) - static_cast<int>(pingus.size()) - get_number_of_exited();
}

int
PinguHolder::get_number_of_alive()
{
  return static_cast<int>(pingus.size());
}

int
PinguHolder::get_number_of_released()
{
  return static_cast<int>(all_pingus.size());
}

int
PinguHolder::get_number_of_allowed()
{
  return number_of_allowed;
}

unsigned int
PinguHolder::get_end_id()
{
  return static_cast<unsigned int>(all_pingus.size());
}

/* EOF */
