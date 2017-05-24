unix_synced_folder_defaults =
  {nfs: true, nfs_version: 4, nfs_udp: false, mount_options: ["noatime", "nodiratime"]}

# Default options
CONFIG['memory'] = "3350"
CONFIG['synced_folder_options'] = windows? ? nil : unix_synced_folder_defaults
CONFIG['additional_disk1_file'] = "./disk1.vdi"
CONFIG['additional_disk1_size'] = 10 * 1024
