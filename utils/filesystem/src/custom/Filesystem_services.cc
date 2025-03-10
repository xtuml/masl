// 
// Filename : Filesystem_services.cc
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
#include "swa/IOError.hh"

#include "Filesystem_OOA/__Filesystem_services.hh"
#include "Filesystem_OOA/__Filesystem_types.hh"

#include "swa/Timestamp.hh"

#include <string>
#include <fstream>
#include <errno.h>
#include <dirent.h>

#ifndef __linux__
  #include <sys/mode.h>
#endif



#include <sys/file.h>
#include <sys/statvfs.h>
#include <sys/stat.h>
#include <unistd.h>
#include <utime.h>
#include <libgen.h>
#include <openssl/md5.h>

#include <iostream>
#include "boost/tokenizer.hpp"
#include "boost/make_shared.hpp"
namespace masld_Filesystem
{

  const bool localServiceRegistration_masls_open_read = interceptor_masls_open_read::instance().registerLocal( &masls_open_read );
  const bool localServiceRegistration_masls_open_write = interceptor_masls_open_write::instance().registerLocal( &masls_open_write );
  const bool localServiceRegistration_masls_open_append = interceptor_masls_open_append::instance().registerLocal( &masls_open_append );
  const bool localServiceRegistration_masls_open_truncate = interceptor_masls_open_truncate::instance().registerLocal( &masls_open_truncate );
  const bool localServiceRegistration_masls_close = interceptor_masls_close::instance().registerLocal( &masls_close );
  const bool localServiceRegistration_masls_set_position = interceptor_masls_set_position::instance().registerLocal( &masls_set_position );
  const bool localServiceRegistration_masls_move_position = interceptor_masls_move_position::instance().registerLocal( &masls_move_position );
  const bool localServiceRegistration_masls_write_file = interceptor_masls_write_file::instance().registerLocal( &masls_write_file );
  const bool localServiceRegistration_masls_touch_file = interceptor_masls_touch_file::instance().registerLocal( &masls_touch_file );
  const bool localServiceRegistration_masls_move_file = interceptor_masls_move_file::instance().registerLocal( &masls_move_file );
  const bool localServiceRegistration_masls_copy_file = interceptor_masls_copy_file::instance().registerLocal( &masls_copy_file );
  const bool localServiceRegistration_masls_copy_file_preserve = interceptor_masls_copy_file_preserve::instance().registerLocal( &masls_copy_file_preserve );
  const bool localServiceRegistration_masls_append_file = interceptor_masls_append_file::instance().registerLocal( &masls_append_file );
  const bool localServiceRegistration_masls_delete_file = interceptor_masls_delete_file::instance().registerLocal( &masls_delete_file );
  const bool localServiceRegistration_masls_set_current_directory = interceptor_masls_set_current_directory::instance().registerLocal( &masls_set_current_directory );
  const bool localServiceRegistration_masls_create_directory = interceptor_masls_create_directory::instance().registerLocal( &masls_create_directory );
  const bool localServiceRegistration_masls_delete_directory = interceptor_masls_delete_directory::instance().registerLocal( &masls_delete_directory );
  const bool localServiceRegistration_masls_set_permissions = interceptor_masls_set_permissions::instance().registerLocal( &masls_set_permissions );
  const bool localServiceRegistration_masls_add_permissions = interceptor_masls_add_permissions::instance().registerLocal( &masls_add_permissions );
  const bool localServiceRegistration_masls_remove_permissions = interceptor_masls_remove_permissions::instance().registerLocal( &masls_remove_permissions );
  const bool localServiceRegistration_masls_change_owner = interceptor_masls_change_owner::instance().registerLocal( &masls_change_owner );
  const bool localServiceRegistration_masls_change_group = interceptor_masls_change_group::instance().registerLocal( &masls_change_group );
  const bool localServiceRegistration_masls_create_sym_link = interceptor_masls_create_sym_link::instance().registerLocal( &masls_create_sym_link );
  const bool localServiceRegistration_masls_create_hard_link = interceptor_masls_create_hard_link::instance().registerLocal( &masls_create_hard_link );
  const bool localServiceRegistration_masls_delete_tree = interceptor_masls_delete_tree::instance().registerLocal( &masls_delete_tree );
  const bool localServiceRegistration_masls_copy_tree = interceptor_masls_copy_tree::instance().registerLocal( &masls_copy_tree );
  const bool localServiceRegistration_masls_copy_tree_preserve = interceptor_masls_copy_tree_preserve::instance().registerLocal( &masls_copy_tree_preserve );
  const bool localServiceRegistration_masls_set_umask = interceptor_masls_set_umask::instance().registerLocal( &masls_set_umask );
  const bool localServiceRegistration_masls_truncate_file = interceptor_masls_truncate_file::instance().registerLocal( &masls_truncate_file );
  const bool localServiceRegistration_masls_unlock_file = interceptor_masls_unlock_file::instance().registerLocal( &masls_unlock_file );

  std::vector<std::string> list_directory ( const std::string& directory_name )
  {
    DIR* directory = opendir ( directory_name.c_str() );

    if ( directory == 0 ) throw SWA::IOError(::std::strerror(errno));

    std::vector<std::string> result;
    dirent* entry;
    while ( (entry = readdir(directory)) != 0 )
    {
      if ( std::string(entry->d_name) != "." && std::string(entry->d_name) != ".." )
      {
        result.push_back(entry->d_name);
      }
    }
    closedir(directory);

    return result;
  } 


  void copy_file (const std::string& source, const std::string& destination, bool preserve )
  {
    std::ifstream src(source.c_str(), std::ios::in | std::ios::binary);
    if ( !src.good() ) throw SWA::IOError(::std::strerror(errno));

    std::ofstream dest(destination.c_str(), std::ios::trunc | std::ios::binary);
    if ( !dest.good() ) throw SWA::IOError(::std::strerror(errno));

    src >> std::noskipws >> dest.rdbuf();

    if ( !dest.good() || src.bad() ) throw SWA::IOError(::std::strerror(errno));

    if ( preserve )
    {
      struct stat status;
      if ( stat(source.c_str(), &status) ) throw SWA::IOError(::std::strerror(errno));
      if ( chmod(destination.c_str(),status.st_mode) ) throw SWA::IOError(::std::strerror(errno));
      if ( chown(destination.c_str(),status.st_uid,status.st_gid) ) throw SWA::IOError(::std::strerror(errno));

      utimbuf timestamps = { status.st_atime, status.st_mtime };
      if ( utime(destination.c_str(), &timestamps) ) throw SWA::IOError(::std::strerror(errno)); 

    }
  }

  void delete_file (const std::string& file_name)
  {
    if ( unlink(file_name.c_str()) && (errno != ENOENT) ) throw SWA::IOError(::std::strerror(errno));
  }

  void delete_directory (const  std::string& directory_name)
  {
    if ( rmdir(directory_name.c_str()) && (errno != ENOENT) ) throw SWA::IOError(::std::strerror(errno));
  }

  void delete_tree ( const std::string& root_name )
  {
    struct stat status;
    if ( lstat(root_name.c_str(), &status) )
    {
      if ( errno == ENOENT ) return;
      else throw SWA::IOError(::std::strerror(errno));
    }

    if ( S_ISDIR(status.st_mode) )
    {
      const std::vector<std::string>& listing = list_directory( root_name );
      for ( std::vector<std::string>::const_iterator it = listing.begin(); it != listing.end(); ++it )
      {
        delete_tree ( root_name + "/" + *it );
      }

      delete_directory(root_name);
    }
    else
    {
      delete_file(root_name);
    }
  }

  void create_directory (const std::string& directory_name)
  {
    boost::tokenizer<boost::char_separator<char> > tokens(directory_name,boost::char_separator<char>( "/", "", boost::keep_empty_tokens ));

    std::string path;
    for ( boost::tokenizer<boost::char_separator<char> >::const_iterator it = tokens.begin(), end = tokens.end(); it != end; ++it )
    {
      path += *it;
      if ( !path.empty() )
      {
        struct stat status;
        if ( stat(path.c_str(), &status) )
        {
          if ( errno == ENOENT )
          {
            if ( mkdir(path.c_str(), S_IRWXU|S_IRWXG|S_IRWXO) )
            {
              throw SWA::IOError(::std::strerror(errno));
            }
          }
          else
          {
            throw SWA::IOError(::std::strerror(errno));
          }
        }
        else if ( !S_ISDIR(status.st_mode) )
        {
          throw SWA::IOError(::std::strerror(EEXIST));
        }
      }
      path += "/";
    }
  }



  void copy_tree ( const std::string& source, const std::string& destination, bool preserve )
  {
    struct stat status;
    if ( lstat(source.c_str(), &status) )
    {
      throw SWA::IOError(::std::strerror(errno));
    }

    if ( S_ISDIR(status.st_mode) )
    {
      create_directory(destination);

      const std::vector<std::string>& listing = list_directory( source );
      for ( std::vector<std::string>::const_iterator it = listing.begin(); it != listing.end(); ++it )
      {
        copy_tree ( source + "/" + *it, destination + "/" + *it, preserve );
      }

      if ( preserve )
      {
        struct stat status;
        if ( stat(source.c_str(), &status) ) throw SWA::IOError(::std::strerror(errno));
        if ( chmod(destination.c_str(),status.st_mode) ) throw SWA::IOError(::std::strerror(errno));
        if ( chown(destination.c_str(),status.st_uid,status.st_gid) ) throw SWA::IOError(::std::strerror(errno));

        utimbuf timestamps = { status.st_atime, status.st_mtime };
        if ( utime(destination.c_str(), &timestamps) ) throw SWA::IOError(::std::strerror(errno)); 

      }


    }
    else
    {
      copy_file(source, destination, preserve);
    }

  }


  void masls_open_read ( const maslt_filename&   maslp_file_name,
                         maslt_file&             maslp_dev )
  {
    try
    {
      maslp_dev.setInputStream(std::make_shared<std::ifstream>(maslp_file_name.c_str(),std::ios::in));
      maslp_dev.clearOutputStream();
    }
    catch ( std::ios::failure& e )
    {
      throw SWA::IOError(strerror(errno));
    }

  }

  void masls_open_write ( const maslt_filename&   maslp_file_name,
                          maslt_file&             maslp_dev )
  {
    try
    {
      if ( masls_file_exists(maslp_file_name) )
      {
        maslp_dev.setInOutStream(std::make_shared<std::fstream>(maslp_file_name.c_str(),std::ios::in|std::ios::out));
      }
      else
      {
        maslp_dev.setInOutStream(std::make_shared<std::fstream>(maslp_file_name.c_str(),std::ios::in|std::ios::out|std::ios::trunc));
      }
    }
    catch ( std::ios::failure& e )
    {
      throw SWA::IOError(strerror(errno));
    }

  }

  void masls_open_append ( const maslt_filename&   maslp_file_name,
                           maslt_file&             maslp_dev )
  {
    try
    {
      if ( masls_file_exists(maslp_file_name) )
      {
        maslp_dev.setInOutStream(std::make_shared<std::fstream>(maslp_file_name.c_str(),std::ios::in|std::ios::out|std::ios::ate));
      }
      else
      {
        maslp_dev.setInOutStream(std::make_shared<std::fstream>(maslp_file_name.c_str(),std::ios::in|std::ios::out|std::ios::trunc));
      }
    }
    catch ( std::ios::failure& e )
    {
      throw SWA::IOError(strerror(errno));
    }

  }

  void masls_open_truncate ( const maslt_filename&   maslp_file_name,
                             maslt_file&             maslp_dev )
  {
    try
    {
      maslp_dev.setInOutStream(std::make_shared<std::fstream>(maslp_file_name.c_str(),std::ios::in|std::ios::out|std::ios::trunc));
    }
    catch ( std::ios::failure& e )
    {
      throw SWA::IOError(strerror(errno));
    }

  }

  void masls_close (maslt_file& device)
  {
    device.clearInputStream();
    device.clearOutputStream();
  }

  void masls_move_position (const maslt_file& dev, int32_t no_chars )
  {
    std::shared_ptr<std::istream> in = dev.getInputStream<std::istream>();
    if ( !in )
    {
      throw SWA::IOError("Not a File");
    }

    in->clear();
    in->seekg(no_chars,std::ios::cur);
  }

  void masls_set_position (const maslt_file& dev, int32_t index )
  {
    std::shared_ptr<std::istream> in = dev.getInputStream<std::istream>();
    if ( !in )
    {
      throw SWA::IOError("Not a File");
    }

    if ( index <= 0 )
    {
      in->clear();
      in->seekg(index,std::ios::end);
    }
    else
    {
      in->clear();
      in->seekg(index-1,std::ios::beg);
    }
  }

  int32_t masls_get_position (const maslt_file& dev )
  {
    std::shared_ptr<std::istream> in = dev.getInputStream<std::istream>();
    if ( !in )
    {
      throw SWA::IOError("Not a File");
    }

    in->clear();
    std::ios::pos_type curPos = in->tellg();
    in->seekg(0,std::ios::beg);
    std::ios::pos_type startPos = in->tellg();
    in->seekg(curPos);
    return static_cast<int32_t>(curPos-startPos)+1;
  }

  int32_t masls_get_length (const maslt_file& dev )
  {
    std::shared_ptr<std::istream> in = dev.getInputStream<std::istream>();
    if ( !in )
    {
      throw SWA::IOError("Not a File");
    }

    in->clear();
    std::ios::pos_type curPos = in->tellg();
    in->seekg(0,std::ios::beg);
    std::ios::pos_type startPos = in->tellg();
    in->seekg(0,std::ios::end);
    std::ios::pos_type endPos = in->tellg();
    in->seekg(curPos);
    return endPos-startPos;
  }

  int32_t masls_get_remaining (const maslt_file& dev )
  {
    std::shared_ptr<std::istream> in = dev.getInputStream<std::istream>();
    if ( !in )
    {
      throw SWA::IOError("Not a File");
    }

    in->clear();
    std::ios::pos_type curPos = in->tellg();
    in->seekg(0,std::ios::end);
    std::ios::pos_type endPos = in->tellg();
    in->seekg(curPos);
    return endPos-curPos;
  }


  bool masls_file_exists (const maslt_filename& maslp_file_name)
  {
    bool exists;
    struct stat buf;
    if ( stat(maslp_file_name.c_str(), &buf) == 0 )
    {
      exists = true;
    }
    else
    {
      if ( errno == ENOENT || errno == ENOTDIR )
      {
        exists = false;
      }
      else
      {
        throw SWA::IOError(::std::strerror(errno));
      }
    } 
    return exists;    
  }



  void masls_touch_file (const maslt_filename& maslp_file_name )
  {
    if ( ! std::ofstream(maslp_file_name.c_str(), std::ios::out|std::ios::app) )
    {
      throw SWA::IOError(strerror(errno));
    }

    if ( utime(maslp_file_name.c_str(),0) )
    {
      throw SWA::IOError(::std::strerror(errno));
    }

  }

  ::SWA::String masls_read_file (const maslt_filename& maslp_file_name )
  {
    std::ifstream file(maslp_file_name.c_str());
    std::ostringstream str;
    file >> std::noskipws >> str.rdbuf();
    if ( file.bad() ) throw SWA::IOError(::std::strerror(errno));
    return str.str();
  }

  void masls_write_file (const maslt_filename& maslp_file_name, const ::SWA::String& maslp_contents )
  {
    std::ofstream file(maslp_file_name.c_str());
    file << maslp_contents << std::flush;
    if ( !file.good() ) throw SWA::IOError(::std::strerror(errno));
  }

  void masls_truncate_file ( const maslt_filename& maslp_file_name,
                             int32_t               maslp_size )
  {
    struct stat status;
    if ( stat(maslp_file_name.c_str(), &status) ) throw SWA::IOError(::std::strerror(errno));

    if ( status.st_size > maslp_size )
    {
      if ( truncate(maslp_file_name.c_str(),maslp_size) ) throw SWA::IOError(::std::strerror(errno));;
    }

  }

  ::SWA::String masls_calculateMD5 ( const maslt_filename& maslp_file_name )
  {
    std::ifstream file(maslp_file_name.c_str());
    MD5_CTX context;
    MD5_Init(&context);
    std::vector<char> buf(32*1024);
    int bytesRead = 0;
    while ( bytesRead = file.readsome(&buf[0],buf.size()) )
    {
      MD5_Update(&context,&buf[0],bytesRead);
    }
    std::vector<unsigned char> digest(MD5_DIGEST_LENGTH);
    MD5_Final(&digest[0],&context);

    std::ostringstream result;
    result << std::hex << std::setw(2) << std::setfill('0');
    for ( std::vector<unsigned char>::iterator it = digest.begin(), end=digest.end(); it != end; ++it )
    {
      result << std::setw(2) << static_cast<int>(*it);
    }
    return SWA::String(result.str());
  }


  void masls_move_file (const maslt_filename& maslp_source, const maslt_filename& maslp_destination)
  {
    if ( rename(maslp_source.c_str(),maslp_destination.c_str()) )
    {
      if ( errno == EXDEV )
      {
        copy_file ( maslp_source.s_str(), maslp_destination.s_str(), true );
        masls_delete_file ( maslp_source );
      }
      else
      {
        throw SWA::IOError(::std::strerror(errno));
      }
    }
  }

  void masls_copy_file (const maslt_filename& maslp_source, const maslt_filename& maslp_destination)
  {
    copy_file ( maslp_source.s_str(), maslp_destination.s_str(), false );
  }

  void masls_copy_file_preserve (const maslt_filename& maslp_source, const maslt_filename& maslp_destination)
  {
    copy_file ( maslp_source.s_str(), maslp_destination.s_str(), true );
  }

  void masls_append_file (const maslt_filename& maslp_source, const maslt_filename& maslp_destination)
  {
    std::ifstream src(maslp_source.c_str(), std::ios::in | std::ios::binary);
    if ( !src.good() ) throw SWA::IOError(::std::strerror(errno));

    std::ofstream dest(maslp_destination.c_str(), std::ios::in|std::ios::out|std::ios::ate|std::ios::binary);
    if ( !dest.good() ) throw SWA::IOError(::std::strerror(errno));

    src >> std::noskipws >> dest.rdbuf();

    if ( !dest.good() || src.bad() ) throw SWA::IOError(::std::strerror(errno));
  }

  void masls_delete_file (const maslt_filename& maslp_file_name)
  {
    delete_file(maslp_file_name.s_str());
  }

  void masls_create_directory (const maslt_filename& maslp_directory_name)
  {
    create_directory(maslp_directory_name.s_str());
  }

  void masls_set_current_directory (const maslt_filename& maslp_directory_name)
  {
    if ( chdir(maslp_directory_name.c_str()) ) throw SWA::IOError(::std::strerror(errno));
  }

  maslt_filename masls_get_current_directory ()
  {
    char result[PATH_MAX+1];
    if ( !getcwd(result,PATH_MAX) ) throw SWA::IOError(::std::strerror(errno));
    return result;
  }

  void masls_delete_directory (const maslt_filename& maslp_directory_name)
  {
    delete_directory(maslp_directory_name.s_str());
  }

  void masls_delete_tree ( const maslt_filename& maslp_root_name )
  {
    delete_tree(maslp_root_name.s_str());
  }

  void masls_copy_tree ( const maslt_filename& maslp_source, const maslt_filename& maslp_destination )
  {
    copy_tree(maslp_source.s_str(), maslp_destination.s_str(),false);
  }

  void masls_copy_tree_preserve ( const maslt_filename& maslp_source, const maslt_filename& maslp_destination )
  {
    copy_tree(maslp_source.s_str(), maslp_destination.s_str(),true);
  }


  mode_t getMode ( const maslt_permissions& permission )
  {
    return (permission.get_masla_sticky()?S_ISVTX:0)
         | (permission.get_masla_setuid()?S_ISUID:0)
         | (permission.get_masla_setgid()?S_ISGID:0)

         | (permission.get_masla_user().get_masla_read()?S_IRUSR:0)
         | (permission.get_masla_user().get_masla_write()?S_IWUSR:0)
         | (permission.get_masla_user().get_masla_execute()?S_IXUSR:0)

         | (permission.get_masla_group().get_masla_read()?S_IRGRP:0)
         | (permission.get_masla_group().get_masla_write()?S_IWGRP:0)
         | (permission.get_masla_group().get_masla_execute()?S_IXGRP:0)

         | (permission.get_masla_other().get_masla_read()?S_IROTH:0)
         | (permission.get_masla_other().get_masla_write()?S_IWOTH:0)
         | (permission.get_masla_other().get_masla_execute()?S_IXOTH:0);
  }

  void masls_set_umask ( const maslt_permissions& malsp_umask )
  {
    umask(getMode(malsp_umask));
  } 

  void masls_add_permissions ( const maslt_filename&    maslp_file_name,
                               const maslt_permissions& maslp_permissions )
  {
    struct stat status;
    if ( stat(maslp_file_name.c_str(), &status) ) throw SWA::IOError(::std::strerror(errno));

    mode_t permissions = getMode(maslp_permissions);

    status.st_mode |= permissions;

    if ( chmod(maslp_file_name.c_str(),status.st_mode) ) throw SWA::IOError(::std::strerror(errno));
  }

  void masls_set_permissions ( const maslt_filename&    maslp_file_name,
                               const maslt_permissions& maslp_permissions )
  {
    mode_t permissions = getMode(maslp_permissions);

    if ( chmod(maslp_file_name.c_str(),permissions) ) throw SWA::IOError(::std::strerror(errno));
  }

  void masls_remove_permissions ( const maslt_filename&    maslp_file_name,
                                  const maslt_permissions& maslp_permissions )
  {
    struct stat status;
    if ( stat(maslp_file_name.c_str(), &status) ) throw SWA::IOError(::std::strerror(errno));

    mode_t permissions = getMode(maslp_permissions);

    status.st_mode &= ~permissions;

    if ( chmod(maslp_file_name.c_str(),status.st_mode) ) throw SWA::IOError(::std::strerror(errno));
  }

  void masls_change_owner ( const maslt_filename&   maslp_file_name,
                            ::masld_Host::maslt_uid maslp_uid )
  {
    if ( chown(maslp_file_name.c_str(),maslp_uid,gid_t(-1)) )
    {
      throw SWA::IOError(::std::strerror(errno));
    }
  }

  void masls_change_group ( const maslt_filename&   maslp_file_name,
                            ::masld_Host::maslt_gid maslp_gid )
  {
    if ( chown(maslp_file_name.c_str(),uid_t(-1),maslp_gid) )
    {
      throw SWA::IOError(::std::strerror(errno));
    }
  }

  maslt_permissions getPermission ( mode_t mode )
  {
    return maslt_permissions ( mode & S_ISVTX, mode & S_ISUID, mode & S_ISGID, 
            maslt_rwx ( mode & S_IRUSR, mode & S_IWUSR, mode & S_IXUSR),  
            maslt_rwx ( mode & S_IRGRP, mode & S_IWGRP, mode & S_IXGRP),  
            maslt_rwx ( mode & S_IROTH, mode & S_IWOTH, mode & S_IXOTH) );  
  }

  maslt_file_status getStatus ( const struct stat& status )
  {
    maslt_file_status result;
    if      ( S_ISREG(status.st_mode) )  result.set_masla_file_type() = maslt_file_types::masle_File;
    else if ( S_ISDIR(status.st_mode) )  result.set_masla_file_type() = maslt_file_types::masle_Directory;
    else if ( S_ISCHR(status.st_mode) )  result.set_masla_file_type() = maslt_file_types::masle_Character;
    else if ( S_ISBLK(status.st_mode) )  result.set_masla_file_type() = maslt_file_types::masle_Block;
    else if ( S_ISFIFO(status.st_mode) ) result.set_masla_file_type() = maslt_file_types::masle_FIFO;
    else if ( S_ISLNK(status.st_mode) )  result.set_masla_file_type() = maslt_file_types::masle_Symlink;
    else if ( S_ISSOCK(status.st_mode) ) result.set_masla_file_type() = maslt_file_types::masle_Socket;

    result.set_masla_permissions() = getPermission(status.st_mode);

    result.set_masla_uid() = status.st_uid;    
    result.set_masla_gid() = status.st_gid;    
    result.set_masla_size() = status.st_size;    

    result.set_masla_access_time() = SWA::Timestamp(status.st_atim);
    result.set_masla_modification_time() = SWA::Timestamp(status.st_mtim);
    result.set_masla_status_change_time() = SWA::Timestamp(status.st_ctim);

    return result;
  }

  maslt_file_status masls_get_file_link_status ( const maslt_filename& maslp_file_name )
  {
    struct stat status;
    if ( lstat(maslp_file_name.c_str(), &status) ) throw SWA::IOError(::std::strerror(errno));
  
    return getStatus ( status );
  }

  maslt_file_status masls_get_file_status ( const maslt_filename& maslp_file_name )
  {
    struct stat status;
    if ( stat(maslp_file_name.c_str(), &status) ) throw SWA::IOError(::std::strerror(errno));

    return getStatus ( status );
  }


  maslt_filesystem_status masls_get_filesystem_status ( const maslt_filename& maslp_file_name )
  {
    struct statvfs status;
    if ( statvfs(maslp_file_name.c_str(), &status) ) throw SWA::IOError(::std::strerror(errno));

    maslt_filesystem_status result;

    result.set_masla_total_bytes()          = status.f_frsize * status.f_blocks;
    result.set_masla_free_bytes()           = status.f_frsize * status.f_bfree;
    result.set_masla_available_bytes()      = status.f_frsize * status.f_bavail;

    result.set_masla_total_nodes()          = status.f_files;
    result.set_masla_free_nodes()           = status.f_ffree;
    result.set_masla_available_nodes()      = status.f_favail;

    result.set_masla_max_filename_length()  = status.f_namemax;
    result.set_masla_read_only()            = status.f_flag & ST_RDONLY;

    return result;
  }

  maslt_filename masls_readlink ( const maslt_filename& maslp_file_name )
  {
    char result[PATH_MAX+1];
    int size = readlink(maslp_file_name.c_str(),result,PATH_MAX);
    if ( size == -1 ) throw SWA::IOError(::std::strerror(errno));
    result[size] = '\0';
    return maslt_filename(result);
  }

  ::SWA::Set< maslt_filename> masls_list_directory ( const maslt_filename& maslp_directory_name )
  {
    SWA::Set<maslt_filename> result(list_directory(maslp_directory_name.s_str()));
    result.forceUnique();
    return result;
  }

  maslt_filename masls_read_link ( const maslt_filename& maslp_link_name )
  {
    std::vector<char> buf(1024);
    ssize_t size;
    while ( ( size = readlink(maslp_link_name.c_str(), &buf[0], buf.size()) ) == static_cast<ssize_t>(buf.size()) )
    {
      // used all the buffer, so try again with a bigger one as may have truncated 
      buf.resize(buf.size()*2);
    }
    if ( size == -1 ) throw SWA::IOError(::std::strerror(errno));
    else return maslt_filename ( buf.begin(), buf.begin()+size);
  }

  maslt_filename masls_canonicalize_filename ( const maslt_filename& maslp_file_name )
  {
    // Relies on glibc extension, as bare POSIX usage is unsafe (see man 3 realpath) 
    char* path = realpath ( maslp_file_name.c_str(), 0 );
    if ( path )
    {
      maslt_filename result(path);
      free(path);
      return result;
    }
    else
    {
      throw SWA::IOError(::std::strerror(errno));
    }

  }

  void masls_create_sym_link ( const maslt_filename& maslp_existing_name,
                               const maslt_filename& maslp_new_name )
  {
    if ( symlink ( maslp_existing_name.c_str(), maslp_new_name.c_str() ) )
    {
      throw SWA::IOError(::std::strerror(errno));
    }
  }

  void masls_create_hard_link ( const maslt_filename& maslp_existing_name,
                                const maslt_filename& maslp_new_name )
  {
    if ( link ( maslp_existing_name.c_str(), maslp_new_name.c_str() ) )
    {
      throw SWA::IOError(::std::strerror(errno));
    }
  }

  maslt_filename masls_get_directory ( const maslt_filename& maslp_file_name )
  {
    // dirname may change arg, so make a copy
    std::vector<char> buf;
    buf.reserve(maslp_file_name.size()+1);
    buf.assign(maslp_file_name.begin(),maslp_file_name.end());
    buf.push_back('\0');
    return maslt_filename(dirname(&buf[0]));
  }

  maslt_filename masls_get_basename ( const maslt_filename& maslp_file_name )
  {
    // basename may change arg, so make a copy
    std::vector<char> buf;
    buf.reserve(maslp_file_name.size()+1);
    buf.assign(maslp_file_name.begin(),maslp_file_name.end());
    buf.push_back('\0');
    return maslt_filename(basename(&buf[0]));
  }

  bool masls_lock_file ( const maslt_filename&   maslp_file_name,
                         const maslt_lock_types& maslp_lock_type,
                         maslt_file_lock&        maslp_lock,
                         bool                    maslp_should_block )
  {

    if ( !masls_file_exists(maslp_file_name) ) throw SWA::IOError("File does not exist");

    int operation;
    if ( maslp_lock_type == maslt_lock_types::masle_ExclusiveLock ) {
      operation = LOCK_EX;
    } else if ( maslp_lock_type == maslt_lock_types::masle_ExclusiveLock ) {
      operation = LOCK_SH;
    } else {
      throw SWA::IOError("Unsupported lock type");
    }

    if ( !maslp_should_block ) {
      operation |= LOCK_NB;
    }

    maslp_lock = open(maslp_file_name.c_str(), O_RDONLY);
    if (maslp_lock < 0) {
      throw SWA::IOError(::std::strerror(errno));
    }

    int ret_val = flock(maslp_lock, operation);
    if (ret_val == 0) {
      return true;
    } else if (errno == EWOULDBLOCK) {
      return false;
    } else {
      throw SWA::IOError(::std::strerror(errno));
    }

  }

  void masls_unlock_file (const maslt_file_lock maslp_lock )
  {
    int ret_val = flock(maslp_lock, LOCK_UN);
    if (ret_val != 0) {
      throw SWA::IOError(::std::strerror(errno));
    }
    ret_val = close(maslp_lock);
    if (ret_val != 0) {
      throw SWA::IOError(::std::strerror(errno));
    }
  }

}
