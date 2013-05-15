//  Pingus - A free Lemmings clone
//  Copyright (C) 1998-2011 Ingo Ruhnke <grumbel@gmx.de>
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

#include "engine/system/sdl_system.hpp"

#include <SDL_image.h>

#include "engine/display/display.hpp"
#include "math/size.hpp"
#include "util/pathname.hpp"
#include "util/log.hpp"

SDLSystem::SDLSystem()
{
  if (SDL_Init(SDL_INIT_VIDEO | SDL_INIT_JOYSTICK) != 0) 
  {
    log_error("Unable to initialize SDL: " << SDL_GetError());
    exit(1);
  }
  else
  {
    atexit(SDL_Quit); 
  }
}

SDLSystem::~SDLSystem()
{
}

void
SDLSystem::create_window(FramebufferType framebuffer_type, const Size& size, bool fullscreen, bool resizable)
{
  SDL_WM_SetCaption("Pingus " VERSION, "Pingus " VERSION);
  SDL_WM_SetIcon(IMG_Load(Pathname("images/icons/pingus.png", Pathname::DATA_PATH).get_sys_path().c_str()), 0);

  Display::create_window(framebuffer_type, size, fullscreen, resizable);

  SDL_EnableUNICODE(1);

  SDL_EnableKeyRepeat(SDL_DEFAULT_REPEAT_DELAY, SDL_DEFAULT_REPEAT_INTERVAL);
}

/* EOF */
