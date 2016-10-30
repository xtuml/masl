//
// UK Crown Copyright (c) 2009. All Rights Reserved
//

//! Services to allow manipulation of the filesystem. If any of 
//! the services fail, they raise an io_error exception. 
domain Filesystem is

  //! Different types of file 
  public type file_types is enum ( Socket, Symlink, File, Block, Directory, Character, FIFO ); 

  //! flags representing the rwx bits of a permission
  public type rwx is structure
    read    : anonymous boolean;
    write   : anonymous boolean;
    execute : anonymous boolean;
  end structure;

  //! representation of the permissions for a particular class of user
  public type permissions is structure
    sticky  : anonymous boolean;
    setuid  : anonymous boolean;
    setgid  : anonymous boolean;
    user    : rwx;
    group   : rwx;
    other   : rwx;
  end structure;

  //! representation of the status of a file
  public type file_status is structure
    file_type          : file_types;
    permissions        : permissions;
    uid                : Host::uid;
    gid                : Host::gid;
    size               : anonymous integer;
    access_time        : anonymous timestamp;
    modification_time  : anonymous timestamp;
    status_change_time : anonymous timestamp;
  end structure;

  //! representation of the status of a filesystem
  public type filesystem_status is structure
    total_bytes         : anonymous long_integer;  // Total size of filesystem
    free_bytes          : anonymous long_integer;  // Free space on filesystem
    available_bytes     : anonymous long_integer;  // Free space available to non-superuser

    total_nodes         : anonymous long_integer;  // Total file nodes on filesystem
    free_nodes          : anonymous long_integer;  // Free file nodes on filesystem
    available_nodes     : anonymous long_integer;  // Free file nodes available to non-superuser

    max_filename_length : anonymous long_integer;
    read_only           : anonymous boolean;
  end structure;

  //! type to hold a a filename
  public type filename is string;

  //! type to hold a file device
  public type file is device;

  //! Opens the file with the specifed name as a read-only device
  public service open_read ( file_name  : in  filename,
                             dev        : out file );

  //! Opens the file with the specifed name as a read/write 
  //! device. The file is created if it does not already exist, 
  //! and the current read/write position is set to the 
  //! beginning of the file. 
  public service open_write ( file_name  : in  filename,
                              dev        : out file );

  //! Opens the file with the specifed name as a read/write 
  //! device. The file is created if it does not already exist. 
  //! The current read/write position is set to the end of the 
  //! file. Note that it is possible to write to the middle of 
  //! the file (ie not append!) by changing the current 
  //! position. 
  public service open_append ( file_name  : in  filename,
                               dev        : out file );

  //! Opens the file with the specifed name as a read/write 
  //! device. The file is created if it does not already exist, 
  //! and any current contents are removed if it does. 
  public service open_truncate ( file_name  : in  filename,
                                 dev        : out file );

  //! Closes the current file and makes it unavailable for reading and writing.
  public service close  (dev  : out file);

  //! Sets the current read/write position to just before the 
  //! indexed character, where index 1 is the first character 
  //! in the file. A negative value indicated that characters 
  //! should be counted from the end of the file, so index zero 
  //! sets the write position at the end of the file (so no 
  //! characters will be available to read) and index -1 sets 
  //! the position just before the last character. 
  public service set_position ( file     : in file,
                                index    : in anonymous integer );

  //! Moves the current read/write position by the specified 
  //! number of characters. Positive values move towards teh 
  //! end of the file, negative towards the start. 
  public service move_position ( file     : in file,
                                 no_chars : in anonymous integer );

  //! Gets the current position in the file, position 1 being 
  //! just before the first character. 
  public function get_position ( file     : in file ) return anonymous integer;

  //! Gets the total number of characters in the file. 
  public function get_length ( file     : in file ) return anonymous integer;

  //! Gets the number of characters remaining to be read from the file. 
  public function get_remaining ( file     : in file ) return anonymous integer;

  //! Returns the entire contents of the file with the specified name as a string. 
  public function read_file (file_name : in filename) return anonymous string;

  //! Creates a file with the specifed name and writes the 
  //! contents to it. If the file already exists, its contents 
  //! are overwritten. 
  public service write_file ( file_name : in filename,
                              contents  : in anonymous string );


  //! Truncates the file to the specified size. If the file is 
  //! already smaller, then it is left unchanged. 
  public service truncate_file ( file_name : in filename,
                                 size      : in anonymous integer );

  //! Returns the hex representation of the MD5 hash of the contents of the supplied file
  public function calculateMD5 (file_name : in filename) return anonymous string;

  //! Indicates whether the specified file exists.
  public function file_exists (file_name : in filename) return anonymous boolean;

  //! Creates an empty file with the specified name. If the 
  //! file already exists its contents are unchanged, but its 
  //! modification time is set to the current time. 
  public service touch_file  (file_name      : in filename);

  //! Moves file named source to destination. If destination exists it is overwritten.
  public service move_file    (source         : in filename,
                               destination    : in filename);

  //! Copies file named source to destination, preserving 
  //! permissions, owner and timestamp from the source. If 
  //! destination exists it is overwritten. 
  public service copy_file     (source         : in filename,
                                destination    : in filename);

  //! Copies file named source to destination. If destination exists it is overwritten. 
  public service copy_file_preserve     (source         : in filename,
                                         destination    : in filename);

  //! Appends the contents of the file named source to the end of destination.
  public service append_file   (source         : in filename,
                                destination    : in filename);

  //! Deletes the named file. If the file does not exist there is no effect.
  public service delete_file   (file_name      : in filename);

  //! Sets the working directory to the specified directory
  public service set_current_directory    (directory_name : in filename);

  //! Returns a list of the files contained within a directory. 
  public function list_directory ( directory_name : in filename ) return anonymous set of filename;
 
  //! Returns the current working directory.
  public function get_current_directory () return filename;

  //! Creates a directory with the specified name, including 
  //! any required parent directories. 
  public service create_directory    (directory_name : in filename);

  //! Deletes the specified directory. If the directory is not 
  //! empty, then an io_error will be raised and the directory 
  //! will not be deleted. 
  public service delete_directory    (directory_name : in filename);

  //! Deletes the named directory or file and any files or directories 
  //! it contains recursively. If any file or directory in the 
  //! tree is not able to be deleted, an io_error will be 
  //! raised and the deletion aborted at that point. 
  public service delete_tree   ( root_name : in filename);

  //! Copies the named directory and any files or directories 
  //! it contains recursively.
  public service copy_tree ( source : in filename, destination : in filename ); 


  //! Copies the named directory and any files or directories 
  //! it contains recursively, preserving permissions, owner 
  //! and timestamp from the source 
  public service copy_tree_preserve ( source : in filename, destination : in filename ); 

  //! Sets the umask for any subsequent file or directory creations
  public service set_umask ( umask : in permissions );

  //! Sets to true any permissions on the specified file which 
  //! are set in the permissions parameters. Any permissions 
  //! which are not set will be left unchanged. 
  public service add_permissions ( file_name   : in filename,
                                   permissions : in permissions );


  //! Sets to false any permissions on the specified file which 
  //! are set in the permissions parameters. Any permissions 
  //! which are not set will be left unchanged. 
  public service remove_permissions ( file_name   : in filename,
                                      permissions : in permissions );

  //! Sets permissions on the specified file to match the 
  //! permissions parameter 
  public service set_permissions ( file_name   : in filename,
                                   permissions : in permissions );

  //! Changes the owner of the file to the specified uid. Note 
  //! that on many systems this may only be done by a 
  //! superuser. 
  public service change_owner ( file_name : in filename, uid : in Host::uid );

  //! Changes the group of the file to the specified gid. Note 
  //! that on many systems, users may only change the group between groups that 
  //! they are a member of. The superuser may change it to any group.
  public service change_group ( file_name : in filename, gid : in Host::gid );

  //! Returns various facts about the file. Follows any 
  //! symbolic links to return information about the underlying 
  //! file. 
  public function get_file_status ( file_name : in filename ) return file_status;

  //! Returns various facts about the file. If the file is a 
  //! link, returns information about the link itself. 
  public function get_file_link_status ( file_name : in filename ) return file_status;

  //! Returns various facts about the filesystem containing file_name.
  public function get_filesystem_status ( file_name : in filename ) return filesystem_status;

  //! Returns the filename that the supplied link points to. If 
  //! the supplied file name is not a link then an exception is 
  //! thrown. 
  public function read_link ( link_name : in filename ) return filename;

  //! Creates a symbolic link to the existing file using the new name
  public service create_sym_link ( existing_name : in filename, new_name : in filename );
  
  //! Creates a hard link to the existing file using the new name
  public service create_hard_link ( existing_name : in filename, new_name : in filename );

  //! Canonicalizes the supplied filename by recursively 
  //! resolving any sym links and references to '.' and '..' 
  public function canonicalize_filename ( file_name : in filename ) return filename;

  //! Returns the directory component of the supplied file name.
  public function get_directory ( file_name : in filename ) return filename;

  //! Returns the filename component of the supplied file name.
  public function get_basename ( file_name : in filename ) return filename;

end domain;
